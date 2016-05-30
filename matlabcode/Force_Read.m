% Script to live plot and store digital values from microcontroller.
% Created by Daniel Darvill 21/01/2016

comport = serial('Serial-COM1', 'BaudRate', 115200); % setup comport
fopen(comport);  % Open comport
x=0;

while(x<300 )
x=x+1;
y1(x)=fscanf(comport, '%d'); % receive digital value

drawnow; 
plot(y1,'r','linewidth',3) % live plot digital value

axis([0 300 0 100]) ;

grid on;
hold on;
title('ADC Testing');
xlabel('Time in tenths of seconds');
ylabel('Force (N)');

end
fclose(comport); % Close comport
delete(comport); % Clear comport