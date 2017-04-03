package com.deadlock;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
/**
 * This is a small application that demonstrates how a deadlock can occur.
 * The app will launch two threads. The first thread Anne will hold lock1 and then sleep. 
 * The second thread Bob will hold lock2 and go to sleep.
 * In order to finish their execution both threads will have to get access to the other lock
 * however since both the resources they need are lock the threads will never complete and the app is deadlocked
 * 
 * @author Francisco Vale
 *
 */
@SpringBootApplication
public class DeadlockApplication {
	// Resource 1
	public static Object lock1 = new Object();
	// Resource 2
	public static Object lock2 = new Object();
	
	private static class Lock{
		public String name;
		public Lock(String name){
			this.name = name;
		}
		
		public void resourceA() {
			// Lock the first resource
			synchronized (lock1) {
				System.out.println(this.name + " holding Lock1");
				// sleep a while
				try { Thread.sleep(10); }
				catch (InterruptedException e) {}
				System.out.println(this.name + " waiting for Lock2" );
				// Attempt to lock second resource
				synchronized (lock2) {
					System.out.println("Thread " + Thread.currentThread().getId() + " holding Lock1 and Lock2");
				}
			}
		}
		
		public void resourceB() {
			// Lock the second resource
			synchronized (lock2) {
				System.out.println(this.name + " holding Lock2");
				// sleep a while
				try { Thread.sleep(10); }
				catch (InterruptedException e) {}
				System.out.println(this.name + " waiting for Lock1" );
				// Attempt to lock first resource
				synchronized (lock1) {
					System.out.println("Thread " + Thread.currentThread().getId() + " holding Lock1 and Lock2");
				}
			}
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(DeadlockApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			final Lock anne = new Lock("Anee");
			final Lock bob	= new Lock("Bob");
			
			new Thread(new Runnable(){
				public void run(){ anne.resourceA(); }
			}).start();
			
			new Thread(new Runnable(){
				public void run() { bob.resourceB(); }
			}).start();
		};
	}
}
