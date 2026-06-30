package com.fatayriTech.avarLMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AvarLmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AvarLmsApplication.class, args);
	}


}


/*
Instead of

Assign now

the admin can choose

Assign:
( ) Immediately

( ) On a future date

( ) Recurring
      Daily
      Weekly
      Monthly

( ) On Employee Join Date

( ) After Probation

( ) After completing another training
*
**/