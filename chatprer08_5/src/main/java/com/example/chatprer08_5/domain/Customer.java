/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.chatprer08_5.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Michael Minella
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

	@NotNull(message="First name is required")
	@Pattern(regexp="[a-zA-Z]+", message="First name must be alphabetical")
	private String firstName;

	@Size(min=1, max=1)
	@Pattern(regexp="[a-zA-Z]", message="Middle initial must be alphabetical")
	private String middleInitial;

	@NotNull(message="Last name is required")
	@Pattern(regexp="[a-zA-Z]+", message="Last name must be alphabetical")
	private String lastName;

	@NotNull(message="Address is required")
	@Pattern(regexp="[0-9a-zA-Z\\. ]+")
	private String address;

	@NotNull(message="City is required")
	@Pattern(regexp="[a-zA-Z\\. ]+")
	private String city;

	@NotNull(message="State is required")
	@Size(min=2,max=2)
	@Pattern(regexp="[A-Z]{2}")
	private String state;

	@NotNull(message="Zip is required")
	@Size(min=5,max=5)
	@Pattern(regexp="\\d{5}")
	private String zip;

	public Customer(Customer customer) {
		this.firstName = customer.firstName;
		this.middleInitial = customer.middleInitial;
		this.lastName = customer.lastName;
		this.address = customer.address;
		this.city = customer.city;
		this.state = customer.state;
		this.zip = customer.zip;}
}
