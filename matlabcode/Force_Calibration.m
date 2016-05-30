% Script to plot and store applied force values against digital values from microcontroller, aswell as create a polynomial fit to the relationship.
% Created by Daniel Darvill 21/01/2016

close all 

% Rearrange digital valuse into usable format
A = zeros(4318,1);
A(1:4000,1) = transpose(LoadFSR1);

% Split time into rising and falling
T_rising = Time1(1:2000);
T_falling = Time1(2000:4318);

% Split Instron input force into rising and falling
Fa_rising = LoadInstron1(1:2000);
Fa_falling = LoadInstron1(2000:4318);

% Split digital values into rising and falling
Fr_rising = A(1:2000);
Fr_falling = A(2000:4318);

%% Fit: 'Applied force to digital value'.
[xData1, yData1] = prepareCurveData( Fr_falling, Fa_falling );
[xData2, yData2] = prepareCurveData( Fr_rising, Fa_rising );

% Set up fittype and options.
ft = fittype( 'poly5' );

% Fit model to data.
[fitresult_falling, gof1] = fit( xData1, yData1, ft );
[fitresult_rising, gof2] = fit( xData2, yData2, ft );


figure 
plot(Fr_falling,Fa_falling)
hold on 
plot(fitresult_falling)
title('falling');
xlabel('Digital Value') ;
ylabel('Force Applied (N)') ;

figure 
plot(Fr_rising,Fa_rising)
hold on 
plot(fitresult_rising)
title('rising');
xlabel('Digital Value') ;
ylabel('Force Applied (N)') ;

