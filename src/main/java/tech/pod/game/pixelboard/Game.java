package tech.pod.game.pixelboard;

import java.util.Scanner;


public class Game {

public static void main(String[] args){

		System.out.println("Bienvenue dans ce Space Invaders !");
		System.out.println("Appuyez sur entrée pour commencer.");
		Scanner sc = new Scanner(System.in);
		String rep = sc.nextLine();

		boolean run = true;

		Thread one = new Thread(){
			public void run() {
				SpaceInvasion.main(null);
			}
		};

		while(run){
			//SpaceInvasion.main(null);

			one.start();
			//one.run();
			try {
				one.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Souhaitez vous rejouez ? (y/n)");
			sc = new Scanner(System.in);
			rep = sc.nextLine();
			if (rep.toUpperCase().equals("N")){ run = false; }
		}

		System.out.println("Au revoir et merci d'avoir joué !");

		sc.close();

	}

}
