import java.util.*;
public class Main1 {
    public static void main(String[]args){
        GardenSystem gs = new GardenSystem();
        GardenPlot plot1 = new GardenPlot("P001", "Sunny Plot", 20.0, "Araba apt hall 216");
        GardenPlot plot2 = new GardenPlot("P002", "Shady Plot", 15.0, "Araba apt hall 217");
        gs.addPlot(plot1);
        gs.addPlot(plot2);

        Gardener g1 = new Gardener("G1", "Akua Oduro", "akua@example.com", "050-000-0000");
        Gardener g2 = new Gardener("G2", "Nana Mensah", "nana@example.com", "024-111-1111");
        gs.registerGardener(g1);
        gs.registerGardener(g2);
        

    }

    
}
