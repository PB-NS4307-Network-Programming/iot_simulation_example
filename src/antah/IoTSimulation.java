package antah;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Random;

public class IoTSimulation {

	static String apiKey = "";
	static boolean redLightOn = false;
	static boolean yellowLightOn = false;
	static boolean greenLightOn = false;
	static boolean soundOn = false;
	static boolean motionDetected = false;
	static boolean servoOpen = false;
	static boolean playMel = false;
	static Random random = new Random();

	public static void main(String[] args) throws Exception {
		new Thread(
				new Runnable() {
					@Override
					public void run() {
						try {
							ServerSocket serverSocket = new ServerSocket(9001);
							while(true) {								
								Socket socket = serverSocket.accept();
								DataInputStream input = new DataInputStream(socket.getInputStream());
									String command = input.readUTF();
									System.out.println(command);
									if(command.equals("redLedOff")) {
										redLightOn = false;
										System.out.println("Red Light Off");
									}
									if(command.equals("redLedOn")) {
										redLightOn = true;
										System.out.println("Red Light On");
									}
									if(command.equals("yellowLedOff")) {
										yellowLightOn = false;
										System.out.println("Yellow Light Off");
									}
									if(command.equals("yellowLedOn")) {
										yellowLightOn = true;
										System.out.println("Yellow Light On");
									}
									if(command.equals("greenLedOff")) {
										greenLightOn = false;
										System.out.println("Green Light Off");
									}
									if(command.equals("greenLedOn")) {
										greenLightOn = true;
										System.out.println("Green Light On");
									}
									if(command.equals("playMelody")) {
										playMel = true;
										System.out.println("Playing melody.");
									}
									if(command.equals("stopMelody")) {
										playMel = false;
										System.out.println("Melody stopped.");
									}
									if(command.equals("toggleServoClose")) {
										servoOpen = false;
										System.out.println("Servo is close.");
									}
									if(command.equals("toggleServoOpen")) {
										servoOpen = true;
										System.out.println("Servo is open.");
									}
							}
						} catch (Exception e) {

						}
					}

				}).start();
		new Thread(
				new Runnable() {
					@Override
					public void run() {
						try {
							while(true) {
								URL url = new URL("http://localhost:8080/sensorData"); 
								HttpURLConnection con = (HttpURLConnection) url.openConnection();
								con.setRequestMethod("POST");
								con.setDoOutput(true);
								DataOutputStream output = new DataOutputStream(con.getOutputStream());
								StringBuilder payload = new StringBuilder();
								double temp = (random.nextDouble(11) + 30);
								double humid = random.nextDouble(101);
								double ultrason = random.nextDouble(1001);
								double water = random.nextDouble(1001);

								soundOn = returnRandBool(random.nextInt(101));
								motionDetected = returnRandBool(random.nextInt(101));

								payload.append("apiKey=" + apiKey + "&");
								payload.append("temp=" + temp + "&");
								payload.append("humid=" + humid + "&");
								payload.append("red=" + redLightOn + "&");
								payload.append("yellow=" + yellowLightOn + "&");
								payload.append("green=" + greenLightOn + "&");
								payload.append("sound=" + soundOn + "&");
								payload.append("ultrason=" + ultrason + "&");
								payload.append("motion=" + motionDetected + "&");
								payload.append("water=" + water + "&");
								payload.append("servo=" + servoOpen + "&");
								payload.append("playMel=" + playMel);
								output.writeBytes(payload.toString());
								output.flush();
								output.close();
//								System.out.println(payload.toString());
								int responseCode = con.getResponseCode();
						        System.out.println("\nSending 'POST' request to URL : " + url);
						        System.out.println("Post parameters : " + payload);
						        System.out.println("Response Code : " + responseCode);
								Thread.sleep(3000);
							}
						} catch (Exception e) {

						}
					}

				}).start();

	}

	private static boolean returnRandBool(int value) {
		if(value % 2 == 0) {
			return true;
		} else {
			return false;
		}
	}

}
