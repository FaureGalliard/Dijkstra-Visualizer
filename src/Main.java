/*
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

*/

import java.util.Scanner;

public class Main{

    public static void compara(int a){

        if(a == 0) System.out.println("es igual a 0");
        else if(a > 0) System.out.println("Es mayor a 0");
        else System.out.println("es menor a 0");
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        compara(a);







    }


}