package com.aashdit.digiverifier.utils;

import lombok.Data;

@Data
public class DateDifference {

	private final int years;
    private final int months;
    private final int days;

    public DateDifference(int years, int months, int days) {
        this.years = years;
        this.months = months;
        this.days = days;
    }

    public DateDifference() {
		this.years = 0;
		this.months = 0;
		this.days = 0;
	}

	public int getYears() {
        return years;
    }

    public int getMonths() {
        return months;
    }

    public int getDays() {
        return days;
    }
}
