
package binarizer;

import org.opencv.core.Core;

public class Binarizer {

    public static void main(String[] args) {
        
        UI wind = new UI();
        wind.buttonControl();
        
        
        try{	
            System.loadLibrary( Core.NATIVE_LIBRARY_NAME );     //załadowanie biblioteki OpenCV

        }catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } 

    }
    
}
