import java.awt.AWTException;
import java.awt.Point;
import java.io.IOException;

import wiiremotej.ButtonMouseMap;
import wiiremotej.ButtonMouseWheelMap;
import wiiremotej.IRAccelerometerMouse;
import wiiremotej.IRLight;
import wiiremotej.IRSensitivitySettings;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WiiRemoteAdapter;

public class MouseControlDemo extends WiiRemoteAdapter {
	//resolution of Wii controller camera
	private static final int X_AXIS = 1024;
	private static final int Y_AXIS = 768;
	private WRAccelerationEvent avgA;
	private WRIREvent avgIR;
	private Smoother smoother;
	private IRAccelerometerMouse mouse;
	private WiiRemote remote;
	private DebugViewer debugView;
	private boolean debugging;

	public static void main(String args[]) {
		WiiRemoteJ.setConsoleLoggingAll();
		WiiRemote remote = null;

		try {
			Util.print("Finding Remote...");
			remote = WiiRemoteJ.findRemote();
			Util.print("Initializing Remote...");
			if (args.length > 0 && args[0].equals("debug")) initRemote(remote, true);
			else initRemote(remote, false);
			Util.print("Mapping remote...");
			mapMouse(remote);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// shut down
		final WiiRemote remoteF = remote;
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				remoteF.disconnect();
			}
		}));
	}

	private static void initRemote(WiiRemote remote, boolean debugging) throws IllegalStateException, IOException {
		MouseControlDemo mcd = new MouseControlDemo(remote);
		mcd.setDebugging(debugging);
		remote.addWiiRemoteListener(mcd);
		remote.setIRSensorEnabled(true, WRIREvent.BASIC, IRSensitivitySettings.WII_LEVEL_5);
		remote.setAccelerometerEnabled(true);
		remote.setLEDIlluminated(0, true);
	}

	private static void mapMouse(WiiRemote remote) throws SecurityException, AWTException {
		remote.getButtonMaps().add(new ButtonMouseMap(WRButtonEvent.B, java.awt.event.InputEvent.BUTTON1_MASK));
		remote.getButtonMaps().add(new ButtonMouseMap(WRButtonEvent.A, java.awt.event.InputEvent.BUTTON3_MASK));
		remote.getButtonMaps().add(new ButtonMouseWheelMap(WRButtonEvent.DOWN, -5, 100));
		remote.getButtonMaps().add(new ButtonMouseWheelMap(WRButtonEvent.UP, 5, 100));
	}

	public MouseControlDemo(WiiRemote remote) {
		smoother = new Smoother();
		debugging = false;
		mouse = null;
		this.remote = remote;
		try {
			mouse = new IRAccelerometerMouse(1.7, 1.7, 0.001, 0.001, Math.PI / 50.0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		remote.setMouse(mouse);
	}

	private void setDebugging(boolean debugging) {
		this.debugging = debugging;
		if (debugging) debugView = new DebugViewer();
	}

	public void accelerationInputReceived(WRAccelerationEvent evt) {
		avgA = evt;
		processMouse();
	}

	public void IRInputReceived(WRIREvent evt) {
		if (debugging) debugView.setRawLights(evt.getIRLights());
		for (int i = 0; i < 4; i++) {
			//there are always 4 lights returned by the controller, but some are null
			if (evt.getIRLights()[i] != null) {
				int x = Math.round((float) (X_AXIS * evt.getIRLights()[i].getX()));
				int y = Math.round((float) (Y_AXIS * evt.getIRLights()[i].getY()));
				smoother.addPoint(new Point(x, y));
			}
		}

		Point p = smoother.getAverage();
		IRLight[] lights = new IRLight[4];

		for (int i = 0; i < 4; i++)
			lights[i] = Util.toIRLight(p);
		avgIR = new WRIREvent(remote, WRIREvent.BASIC, lights);
		processMouse();
	}

	private void processMouse() {
		if (avgIR != null && avgA != null) try {
			mouse.processMouseEvent(avgIR, avgA);
			if (debugging) {
				debugView.setLights(avgIR.getIRLights());
				debugView.repaint();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
