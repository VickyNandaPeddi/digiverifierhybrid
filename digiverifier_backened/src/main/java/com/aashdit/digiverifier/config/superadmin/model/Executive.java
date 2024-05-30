package com.aashdit.digiverifier.config.superadmin.model;

import com.aashdit.digiverifier.config.superadmin.Enum.ExecutiveName;
import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "t_dgv_executive")
public class Executive {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "executive_id")
	private Long executiveId;
	
	@Column(name = "name")
	@Enumerated(EnumType.STRING)
	private ExecutiveName name;
	
}
