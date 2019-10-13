package com.sonardraft;

import org.opencv.core.Core;

public class Initializer {

	public static void main(String[] args) {
		// Initialize needed libraries
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Start all listeners

		Thread listeners = new Thread() {

			public void run() {
				while (Tools.programmRunning) {
					Tools.isClientRunning();
				}
			}
		};

		listeners.start();

		Thread programm = new Thread() {

			public void run() {
				Tools.start();
			}
		};

		programm.start();

	}

}
