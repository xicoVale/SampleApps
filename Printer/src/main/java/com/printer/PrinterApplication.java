package com.printer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * This is a simple thread spooler. It will generate 1000 threads, they will compete for
 * access to a string where they will write their thread number. The threads will then print that string
 * 
 * @author Francisco Vale
 *
 */

@SpringBootApplication
public class PrinterApplication {
	// Lock to prevent concurrency issues
	private Lock lock = new ReentrantLock();
	// Output message
	protected String message;
	
	private final class Writter implements Runnable {
		
		public Writter() {}
		
		public void print() {
			try {
				// Threads will sleep for a while after being created
				// This is to simulate a heavier load enviornment
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				// Attempt a lock
				if(lock.tryLock(1000, TimeUnit.MILLISECONDS)){
					message = "Hello I am thread " + Thread.currentThread().getId();
					System.out.println(message);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				// Release the lock
				lock.unlock();
			}
		}
		
		public void run() {
			print();
		}
	}
	
	public static void main(String[] args) {
		SpringApplication.run(PrinterApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			// Generate 1000 threads (small number I know)
			for(int i = 0; i < 1000; i++) {
				new Thread(new Writter()).start();
			}
		};
	}
}
