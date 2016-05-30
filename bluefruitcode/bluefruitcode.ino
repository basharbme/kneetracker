    /*********************************************************************
 This is an example for our nRF51822 based Bluefruit LE modules

 Pick one up today in the adafruit shop!

 Adafruit invests time and resources providing this open source code,
 please support Adafruit and open-source hardware by purchasing
 products from Adafruit!

 MIT license, check LICENSE for more information
 All text above, and the splash screen below must be included in
 any redistribution
*********************************************************************/
/* ---------------- Accelerometer libraries -----------------
 *  --------------------------------------------------------- */
#include <Wire.h>
#include <ADXL345.h>

/* ---------------- Bluetooth LE libraries ------------------
 *  --------------------------------------------------------- */
#include <Arduino.h>
#include <SPI.h>
#if not defined (_VARIANT_ARDUINO_DUE_X_) && not defined (_VARIANT_ARDUINO_ZERO_)
  #include <SoftwareSerial.h>
#endif

#include "Adafruit_BLE.h"
#include "Adafruit_BluefruitLE_SPI.h"
#include "Adafruit_BluefruitLE_UART.h"

#include "BluefruitConfig.h"

/* ---------------- General Defines -------------------------
 *  --------------------------------------------------------- */
 #define BUFF_SIZE 8

/*=========================================================================
    APPLICATION SETTINGS

    FACTORYRESET_ENABLE       Perform a factory reset when running this sketch
   
                              Enabling this will put your Bluefruit LE module
                              in a 'known good' state and clear any config
                              data set in previous sketches or projects, so
                              running this at least once is a good idea.
   
                              When deploying your project, however, you will
                              want to disable factory reset by setting this
                              value to 0.  If you are making changes to your
                              Bluefruit LE device via AT commands, and those
                              changes aren't persisting across resets, this
                              is the reason why.  Factory reset will erase
                              the non-volatile memory where config data is
                              stored, setting it back to factory default
                              values.
       
                              Some sketches that require you to bond to a
                              central device (HID mouse, keyboard, etc.)
                              won't work at all with this feature enabled
                              since the factory reset will clear all of the
                              bonding data stored on the chip, meaning the
                              central device won't be able to reconnect.
    MINIMUM_FIRMWARE_VERSION  Minimum firmware version to have some new features
    MODE_LED_BEHAVIOUR        LED activity, valid options are
                              "DISABLE" or "MODE" or "BLEUART" or
                              "HWUART"  or "SPI"  or "MANUAL"
    -----------------------------------------------------------------------*/
    #define FACTORYRESET_ENABLE         1
    #define MINIMUM_FIRMWARE_VERSION    "0.6.6"
    #define MODE_LED_BEHAVIOUR          "MODE"
/*=========================================================================*/

// Create the bluefruit object, either software serial...uncomment these lines
/*
SoftwareSerial bluefruitSS = SoftwareSerial(BLUEFRUIT_SWUART_TXD_PIN, BLUEFRUIT_SWUART_RXD_PIN);

Adafruit_BluefruitLE_UART ble(bluefruitSS, BLUEFRUIT_UART_MODE_PIN,
                      BLUEFRUIT_UART_CTS_PIN, BLUEFRUIT_UART_RTS_PIN);
*/

/* ...or hardware serial, which does not need the RTS/CTS pins. Uncomment this line */
// Adafruit_BluefruitLE_UART ble(Serial1, BLUEFRUIT_UART_MODE_PIN);

/* ...hardware SPI, using SCK/MOSI/MISO hardware SPI pins and then user selected CS/IRQ/RST */
Adafruit_BluefruitLE_SPI ble(BLUEFRUIT_SPI_CS, BLUEFRUIT_SPI_IRQ, BLUEFRUIT_SPI_RST);

/* ...software SPI, using SCK/MOSI/MISO user-defined SPI pins and then user selected CS/IRQ/RST */
//Adafruit_BluefruitLE_SPI ble(BLUEFRUIT_SPI_SCK, BLUEFRUIT_SPI_MISO,
//                             BLUEFRUIT_SPI_MOSI, BLUEFRUIT_SPI_CS,
//                             BLUEFRUIT_SPI_IRQ, BLUEFRUIT_SPI_RST);

// adxl is an instance of the ADXL345 library
ADXL345 adxl; 

// Pin that force sensor is connected to
int force_sensor_pin = A1;
// Variable to store data from force sensor
int force_sensor_value = 0;
// Are we sending accelerometer or force sensor data over BLE
boolean send_force_data = false;

// Determines the rate at which we send data to bluetooth
unsigned int cnt;

// Stores location in force array
unsigned char force_idx = 0;

// Last accelerometer value
int prevAccelVal = 0;

// Threshold above which a movment is considered significant, 10 is best
int ACCEL_THRESHOLD = 12;

// Number of counts after a direction signal during which to ignore new values (change back to 200 for BLE)
int COUNT_THRESHOLD = 200;

// Counter for accelerometer turn off period
int accelCounter = 0;

// Should the accelerometer be looking for movements?
bool isReading = true;

char output[11];
int i = 0;

// The circular buffer for smoothing
int buff [BUFF_SIZE];
// The index into the circular smoothing buffer
int buffIdx = 0;
// How far back in history to calculate the gradient (must be < BUFF_SIZE - 1)
int lag = 8;


// A small helper
void error(const __FlashStringHelper*err) {
  Serial.println(err);
  while (1);
}

/**************************************************************************/
/*!
    @brief  Sets up the HW an the BLE module (this function is called
            automatically on startup)
*/
/**************************************************************************/
void setup(void)
{
    cnt = 0;
    // Power on the accelerometer
    adxl.powerOn();
  
    //while (!Serial);  // required for Flora & Micro
    //delay(500);

    //Serial.begin(115200);
    //Serial.println(F("Adafruit Bluefruit Command Mode Example"));
    //Serial.println(F("---------------------------------------"));

    /* Initialise the module */
    //Serial.print(F("Initialising the Bluefruit LE module: "));

    if ( !ble.begin(VERBOSE_MODE) )
    {
        error(F("Couldn't find Bluefruit, make sure it's in CoMmanD mode & check wiring?"));
    }
    //Serial.println( F("OK!") );

    if ( FACTORYRESET_ENABLE )
    {
        /* Perform a factory reset to make sure everything is in a known state */
        //Serial.println(F("Performing a factory reset: "));
        if ( ! ble.factoryReset() ){
        error(F("Couldn't factory reset"));
        }
    }

    /* Disable command echo from Bluefruit */
    ble.echo(false);

    //Serial.println("Requesting Bluefruit info:");
    /* Print Bluefruit information */
    //ble.info();

    //Serial.println(F("Please use Adafruit Bluefruit LE app to connect in UART mode"));
    //Serial.println(F("Then Enter characters to send to Bluefruit"));
    //Serial.println();

    //ble.verbose(false);  // debug info is a little annoying after this point!

    /* Wait for connection */
    while (! ble.isConnected()) {
        delay(500);
    }

    // LED Activity command is only supported from 0.6.6
    if ( ble.isVersionAtLeast(MINIMUM_FIRMWARE_VERSION) )
    {
        // Change Mode LED Activity
        //Serial.println(F("******************************"));
        //Serial.println(F("Change LED activity to " MODE_LED_BEHAVIOUR));
        ble.sendCommandCheckOK("AT+HWModeLED=" MODE_LED_BEHAVIOUR);
        //Serial.println(F("******************************"));
    }

    // 10 ms connection interval
    //ble.print("AT+GAPINTERVALS=10,20,,");

//    // Set module to DATA mode
//    Serial.println( F("Switching to DATA mode!") );
//    ble.setMode(BLUEFRUIT_MODE_DATA);
//
//    Serial.println(F("******************************"));
}

/**************************************************************************/
/*!
    @brief  Constantly poll for new command or response data
*/
/**************************************************************************/
void loop(void)
{
     /***************************** RECEIVING ******************************************/
     /**********************************************************************************/
     ble.println("AT+BLEUARTRX");
     ble.readline();
     if (strcmp(ble.buffer, "OK") != 0) {
         // Some data was found, its in the buffer
         //Serial.print(F("[Recv] ")); Serial.println(ble.buffer);
         if (ble.buffer[0] == 'f') {
             send_force_data = true;
         } else if (ble.buffer[0] == 'a') {
            /* In this case we want to send accelerometer data */
            send_force_data = false;
            // Reset acceleromter counter
            accelCounter = 0;
         }
     }
     
    /***************************** SENDING *********************************************/
    /***********************************************************************************/
    if (send_force_data) {
//        float p1= 7.9977e-12;
//        float p2= -9.6318e-9;
//        float p3= 4.2943e-6;
//        float p4= -7.4957e-4;
//        float p5= 0.0716;
//        float p6= 0.1952; 
        // Map 10 bit number to 8 bit number
        int sv = analogRead(force_sensor_pin);
        // Calibration
        //float force = p1*pow(sv,5) + p2*pow(sv,4) + p3*pow(sv,3) + p4*pow(sv,2) + p5*pow(sv,1) + p6 ;
        ble.print("AT+BLEUARTTX=");
        ble.println(sv);
        // Delay 100 milli
        delay(150);
    } else {
        // Accelerometer y value only required
        int x, y, z;  
        adxl.readAccel(&x, &y, &z);
        // **** Filter the accelerometer value **********
        buff[buffIdx] = y;
        buffIdxIncrement();
        y = (int) getBuffMean();
        //Serial.println(accelCounter);
        // **********************************************
        
        // ************** Movement detection *****************************
        if (isReading) {
            char directionStr [16];
            int diff = y - prevAccelVal;
            //Serial.println(diff);
            if (diff > ACCEL_THRESHOLD || diff < -ACCEL_THRESHOLD) {
                if (diff > ACCEL_THRESHOLD) {
                    sprintf(directionStr, "Forward");
                } else {
                    sprintf(directionStr, "Backwards");
                }
                //Serial.println(directionStr);
                ble.print("AT+BLEUARTTX=");
                ble.println(directionStr);
                // Turn off reading for a short period
                isReading = false;   
                accelCounter = 0; 
            }
          // If the waiting period is over
        } else if (accelCounter++ > COUNT_THRESHOLD) {
           isReading = true;
           accelCounter = 0;
        }
        // Handles circular wraparound
        if (buffIdx >= lag) { 
            prevAccelVal = buff[buffIdx - lag];
        } else {
            prevAccelVal = buff[BUFF_SIZE + buffIdx - lag];
        }
        // ************** End movement detection *************************

        // OLD CODE ******************************************************
//        // Echo accelerometer data to console
//        Serial.println(y);                                            
//        ble.print("AT+BLEUARTTX=");
//        ble.println(y);
        // ***************************************************************
    }
}

// Get the mean of the circular buffer (for smoothing)
float getBuffMean() {
    float sum = 0;
    for (int j=0; j<BUFF_SIZE; ++j) {
        sum += (float) buff[j];
    }
    return sum / (float) BUFF_SIZE;
}

// Circularly increments buffer index
void buffIdxIncrement() {
    if (++buffIdx == BUFF_SIZE) {
        buffIdx = 0;
    }
}

