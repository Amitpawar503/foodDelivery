package com.food.delivery;

import org.springframework.boot.SpringApplication;

public class TestFoodDeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.from(FoodDeliveryApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
