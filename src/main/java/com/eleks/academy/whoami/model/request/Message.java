package com.eleks.academy.whoami.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

	@NotBlank
	@Size(min = 2, max = 256, message = "message must be between 2 and 256 characters long")
	private String message;

}
