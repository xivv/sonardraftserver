package com.sonardraft;

import org.opencv.core.Core;

public class Initializer {

	public static void main(String[] args) {
		// Initialize needed libraries
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		TemplateRecognition.init();
		TemplateRecognition.check();
	}

}
