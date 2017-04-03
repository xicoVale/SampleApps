package com.deadlock.fixed;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * This is a working version of the deadlock app
 * The locks are now no longer of the Object type, now they are ReentrantLocks.
 * ReentrantLock is an implementation of Lock that provides the ability to retry obtaining locks.
 * 
 * @author Francisco Vale
 *
 */
@SpringBootApplication
public class DeadlockFixedApplication {
		// Resource 1
		private static final Lock lock1 = new ReentrantLock();
		// Resource 2
		private static final Lock lock2 = new ReentrantLock();
		
		private static class GoodThread{
			public String name;
			public GoodThread(String name){
				this.name = name;
			}
			
			public void resourceA() {
				// Lock the first resource
				try {
					// The thread will attempt to lock the first resource 
					// If it is unsuccessful then it will try again for 10 milliseconds
					if(lock1.tryLock(10, TimeUnit.MILLISECONDS)) {
						System.out.println(this.name + " holding Lock1");
						// sleep a while
						try { Thread.sleep(10); }
						catch (InterruptedException e) {}
						System.out.println(this.name + " waiting for Lock2" );
						// Attempt to lock second resource
						synchronized (lock2) {
							System.out.println(this.name + " holding Lock1 and Lock2");
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finally {
					lock1.unlock();
				}
			}
			
			public void resourceB() {
				// Lock the second resource
				try {
					// The thread will attempt to lock the second resource 
					// If it is unsuccessful then it will try again for 10 milliseconds
					if (lock2.tryLock(10, TimeUnit.MILLISECONDS)) {
						System.out.println(this.name + " holding Lock2");
						// sleep a while
						try { Thread.sleep(10); }
						catch (InterruptedException e) {}
						System.out.println(this.name + " waiting for Lock1" );
						// Attempt to lock first resource
						synchronized (lock1) {
							System.out.println(this.name + " holding Lock1 and Lock2");
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finally {
					lock2.unlock();
				}
			}
		}

	public static void main(String[] args) {
		SpringApplication.run(DeadlockFixedApplication.class, args);
	}
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			final GoodThread anne = new GoodThread("Anee");
			final GoodThread bob  = new GoodThread("Bob");
			
			new Thread(new Runnable(){
				public void run(){ anne.resourceA(); }
			}).start();
			
			new Thread(new Runnable(){
				public void run() { bob.resourceB(); }
			}).start();
		};
	}
}
