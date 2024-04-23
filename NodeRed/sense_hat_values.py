from sense_hat import SenseHat
import time

sense = SenseHat()

while(True):
	pressure = sense.get_pressure()
	gyro = sense.get_gyroscope()

	print("Pressure: {:.2f} Millibars, Gyroscope: {}".format(pressure, gyro))
	time.sleep(0.1)
