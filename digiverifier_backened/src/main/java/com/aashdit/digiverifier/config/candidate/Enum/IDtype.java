package com.aashdit.digiverifier.config.candidate.Enum;

public enum IDtype {
	AADHAAR("Aadhaar"),
	PAN("PAN"),
	UAN("UAN"),
	DRIVING_LICENSE("Driving License"),
	PASSPORT("Passport"),
	ALP("Aadhar linked Pan");
	
	public final String label;
	
	private IDtype(String label) {
		this.label = label;
	}
}
