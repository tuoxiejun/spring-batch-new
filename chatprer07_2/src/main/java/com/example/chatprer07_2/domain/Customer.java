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
package com.example.chatprer07_2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Michael Minella
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
	private String firstName;
	private String middleInitial;
	private String lastName;
//	private String addressNumber;
//	private String street;

	private String address;
	private String city;
	private String state;
	private String zipCode;

//	private List<Transaction> transactions;

}