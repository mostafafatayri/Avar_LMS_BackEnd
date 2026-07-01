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


//Cron	Meaning
//  0 * * * * *	Every minute
//  0 */5 * * * *	Every 5 minutes
//  0 */10 * * * *	Every 10 minutes
//  00 */30 * * * *	Every 30 minutes
/// 0 0 * * * *	Every hour
/// 0 0 8 * * *	Every day at 8:00 AM
/// 0 0 9 * * MON-FRI	Weekdays at 9:00 AM
//  0/30 * * * * *
