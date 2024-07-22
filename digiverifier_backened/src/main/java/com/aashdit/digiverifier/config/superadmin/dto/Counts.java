package com.aashdit.digiverifier.config.superadmin.dto;

import lombok.Data;

@Data
public class Counts {
    private int qcCreatedCount;
    private int processDeclinedCount;
    private int invitationExpiredCount;
}
