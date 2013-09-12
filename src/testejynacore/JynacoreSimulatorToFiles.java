/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testejynacore;

import br.ufjf.mmc.jynacore.JynaSimulableModel;
import br.ufjf.mmc.jynacore.JynaSimulation;
import br.ufjf.mmc.jynacore.JynaSimulationData;
import br.ufjf.mmc.jynacore.JynaSimulationMethod;
import br.ufjf.mmc.jynacore.JynaSimulationProfile;
import br.ufjf.mmc.jynacore.JynaValued;
import br.ufjf.mmc.jynacore.impl.DefaultSimulationData;
import br.ufjf.mmc.jynacore.impl.DefaultSimulationProfile;
import br.ufjf.mmc.jynacore.metamodel.instance.ClassInstanceItem;
import br.ufjf.mmc.jynacore.metamodel.instance.MetaModelInstance;
import br.ufjf.mmc.jynacore.metamodel.instance.MetaModelInstanceStorer;
import br.ufjf.mmc.jynacore.metamodel.instance.impl.DefaultMetaModelInstanceStorerJDOM;
import br.ufjf.mmc.jynacore.metamodel.simulator.impl.DefaultMetaModelInstanceEulerMethod;
import br.ufjf.mmc.jynacore.metamodel.simulator.impl.DefaultMetaModelInstanceSimulation;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author igor
 */
public class JynacoreSimulatorToFiles {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        JynaSimulation simulation = new DefaultMetaModelInstanceSimulation();
        JynaSimulationProfile profile = new DefaultSimulationProfile();
        JynaSimulationMethod method = new DefaultMetaModelInstanceEulerMethod();
        JynaSimulableModel instance;
        DefaultSimulationData data = new DefaultSimulationData();

        MetaModelInstanceStorer storer = new DefaultMetaModelInstanceStorerJDOM();
        String modelFile = "C:\\modelo-simples\\Instância de Projeto Simples com Cenários 1.jymmi";
        String propName = "Trabalho";
        String filePrefix = "teste";
//      try {
//         modelFile = args[0];
//         propName = args[1];
//         filePrefix = args[2];
//      } catch (Exception e) {
//         System.out.println("Usage: " + modelFile + "<metamodelinstance> <property> <prefix data files>\n");
//         return;
//      }

        File f = new File(modelFile);
        f.getAbsolutePath();
        
        
        
        instance = storer.loadFromFile(f);
        profile.setInitialTime(0.0);
        profile.setFinalTime(500.0);
        profile.setTimeLimits(1000, 10.0);
        int skip = 100;
        
        

        //simulation.

        simulation.setMethod(method);
        simulation.setProfile(profile);
        data.removeAll();
        data.clearAll();
        for (JynaValued jv : instance.getAllJynaValued()) {
            //System.out.println(jv.getValue());
            ClassInstanceItem cii = (ClassInstanceItem) jv;
            if (cii.getName().equals(propName)) {
                data.add(cii.getClassInstance().getName() + "." + cii.getName(), jv);
                System.out.println(cii.getClassInstance().getName() + "." + cii.getName() + "\n");
            }
            //System.out.println(cii.getClassInstance().getName() + "." + cii.getName() + "| Valor: "+ jv.getValue());
            System.out.println("Nome: " + jv.getName() + "| Valor: " + jv.getValue());
        }

        simulation.setModel(instance);
        simulation.setSimulationData(
                (JynaSimulationData) data);
        simulation.reset();

        data.register(0.0);
        runSimulation(simulation, skip);

        try {
            File file = new File(filePrefix + ".dat");
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(data.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        storer.saveToFile((MetaModelInstance) instance, new File("fileTeste"));

    }

    private static void runSimulation(JynaSimulation simulation, int skip) throws Exception {
        //simulation.run();
        int steps = simulation.getProfile().getTimeSteps();

        System.out.println("Simulating with " + simulation.getProfile().getTimeSteps() + " iterations. Interval " + simulation.getProfile().getTimeInterval() + " to " + simulation.getProfile().getFinalTime());
        for (int i = 0;
                i < steps;
                i++) {
            simulation.step();
            if (i % skip == 0) {
                simulation.register();
            }
        }
        //System.out.println("Simulating done!");
    }
}
