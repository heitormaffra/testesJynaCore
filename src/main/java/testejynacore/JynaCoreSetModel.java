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
import br.ufjf.mmc.jynacore.expression.BooleanOperator;
import br.ufjf.mmc.jynacore.expression.Expression;
import br.ufjf.mmc.jynacore.expression.NumberOperator;
import br.ufjf.mmc.jynacore.expression.impl.DefaultExpression;
import br.ufjf.mmc.jynacore.expression.impl.DefaultNameExpression;
import br.ufjf.mmc.jynacore.expression.impl.DefaultNumberConstantExpression;
import br.ufjf.mmc.jynacore.impl.DefaultSimulationData;
import br.ufjf.mmc.jynacore.impl.DefaultSimulationProfile;
import br.ufjf.mmc.jynacore.metamodel.MetaModel;
import br.ufjf.mmc.jynacore.metamodel.MetaModelClass;
import br.ufjf.mmc.jynacore.metamodel.MetaModelClassAuxiliary;
import br.ufjf.mmc.jynacore.metamodel.MetaModelClassProperty;
import br.ufjf.mmc.jynacore.metamodel.MetaModelClassRate;
import br.ufjf.mmc.jynacore.metamodel.MetaModelClassStock;
import br.ufjf.mmc.jynacore.metamodel.MetaModelRelation;
import br.ufjf.mmc.jynacore.metamodel.MetaModelScenario;
import br.ufjf.mmc.jynacore.metamodel.MetaModelScenarioAffect;
import br.ufjf.mmc.jynacore.metamodel.MetaModelScenarioConnection;
import br.ufjf.mmc.jynacore.metamodel.impl.DefaultMetaModel;
import br.ufjf.mmc.jynacore.metamodel.impl.DefaultMetaModelClass;
import br.ufjf.mmc.jynacore.metamodel.impl.DefaultMetaModelClassAuxiliary;
import br.ufjf.mmc.jynacore.metamodel.impl.DefaultMetaModelClassProperty;
import br.ufjf.mmc.jynacore.metamodel.impl.DefaultMetaModelClassRate;
import br.ufjf.mmc.jynacore.metamodel.impl.DefaultMetaModelClassStock;
import br.ufjf.mmc.jynacore.metamodel.impl.DefaultMetaModelMultiRelation;
import br.ufjf.mmc.jynacore.metamodel.impl.DefaultMetaModelScenario;
import br.ufjf.mmc.jynacore.metamodel.impl.DefaultMetaModelScenarioAffect;
import br.ufjf.mmc.jynacore.metamodel.impl.DefaultMetaModelScenarioConnection;
import br.ufjf.mmc.jynacore.metamodel.impl.DefaultMetaModelSingleRelation;
import br.ufjf.mmc.jynacore.metamodel.instance.ClassInstance;
import br.ufjf.mmc.jynacore.metamodel.instance.ClassInstanceItem;
import br.ufjf.mmc.jynacore.metamodel.instance.MetaModelInstance;
import br.ufjf.mmc.jynacore.metamodel.instance.impl.DefaultMetaModelInstance;
import br.ufjf.mmc.jynacore.metamodel.simulator.MetaModelInstanceSimulationMethod;
import br.ufjf.mmc.jynacore.metamodel.simulator.impl.DefaultMetaModelInstanceEulerMethod;
import br.ufjf.mmc.jynacore.metamodel.simulator.impl.DefaultMetaModelInstanceSimulation;

/**
 *
 * @author heitormaffra
 */
public class JynaCoreSetModel {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        try {
            JynaSimulationProfile profile = new DefaultSimulationProfile();
            JynaSimulationData data = new DefaultSimulationData();
            JynaSimulation simulation = new DefaultMetaModelInstanceSimulation();
            JynaSimulationMethod method = new DefaultMetaModelInstanceEulerMethod();

            // Modelo de Domínio
            br.ufjf.mmc.jynacore.metamodel.MetaModel domainModel = new DefaultMetaModel();
            domainModel.setName("Projeto de Software Simples");

            // Desenvolvedor Class
            MetaModelClass developer = new DefaultMetaModelClass();
            developer.setName("Desenvolvedor");

            MetaModelClassProperty experience = new DefaultMetaModelClassProperty();
            experience.setName("experiência");
            experience.setDefaultValue(1.0);
            developer.put(experience);

            MetaModelClassAuxiliary productivity = new DefaultMetaModelClassAuxiliary();
            productivity.setName("Produtividade");
            productivity
                    .setExpression(new DefaultNameExpression("experiência"));
            developer.put(productivity);

            domainModel.put(developer);

            // Atividade Class
            MetaModelClass activity = new DefaultMetaModelClass();
            activity.setName("Atividade");

            MetaModelClassProperty duration = new DefaultMetaModelClassProperty();
            duration.setName("duração");
            duration.setDefaultValue(25.0);
            activity.put(duration);

            MetaModelClassStock timeToConclude = new DefaultMetaModelClassStock();
            timeToConclude.setName("Tempo para Concluir");
            timeToConclude.setExpression(new DefaultNameExpression("duração"));
            activity.put(timeToConclude);

            MetaModelClassAuxiliary production = new DefaultMetaModelClassAuxiliary();
            production.setName("Produção");
            production.setExpression(new DefaultNumberConstantExpression(1.0));
            activity.put(production);

            MetaModelClassRate work = new DefaultMetaModelClassRate();
            work.setName("Trabalho");
            work.setSource(timeToConclude);
            Expression workExp = new DefaultExpression();
            workExp.setOperator(NumberOperator.MINIMUM);
            workExp.setLeftOperand(new DefaultNameExpression("Produção"));
            workExp.setRightOperand(new DefaultNameExpression(
                    "Tempo para Concluir"));
            work.setExpression(workExp);
            activity.put(work);

            domainModel.put(activity);

            MetaModelRelation team = new DefaultMetaModelSingleRelation();
            team.setName("Equipe");
            team.setSource(activity);
            team.setTarget(developer);
            domainModel.put(team);

            MetaModelRelation precedence = new DefaultMetaModelMultiRelation();
            precedence.setName("Precedente");
            precedence.setSource(activity);
            precedence.setTarget(activity);
            domainModel.put(precedence);

            // Modelo de Instância
            MetaModelInstance instanceModel = new DefaultMetaModelInstance();
            instanceModel.setMetaModel(domainModel);
            instanceModel.setName("Instância de Projeto com Cenários");

            instanceModel.addNewClassInstance("D1", "Desenvolvedor");
            instanceModel.addNewClassInstance("D2", "Desenvolvedor");

            instanceModel.getClassInstances().get("D1").setProperty(
                    "experiência", 1.0);
            instanceModel.getClassInstances().get("D2").setProperty(
                    "experiência", 0.5);

            instanceModel.addNewClassInstance("Projeto", "Atividade");
            instanceModel.addNewClassInstance("Codificação", "Atividade");

            ClassInstance design = instanceModel.getClassInstances().get(
                    "Projeto");
            design.setProperty("duração", 20.0);
            design.setLink("Equipe", "D1");

            ClassInstance coding = instanceModel.getClassInstances().get(
                    "Codificação");
            coding.setProperty("duração", 15.0);
            coding.setLink("Precedente", "Projeto");
            coding.setLink("Equipe", "D2");

            profile.setTimeLimits(50, 50.0);

            // Modelo de Cenário
            MetaModelScenario sceActTeam = new DefaultMetaModelScenario();
            sceActTeam.setName("Produção Baseada na Equipe");
            sceActTeam.setMetaModel(domainModel);

            MetaModelScenarioConnection connProdByTeam = new DefaultMetaModelScenarioConnection();
            connProdByTeam.setName("AAtividade");
            connProdByTeam.setClassName("Atividade");
            MetaModelScenarioAffect affEquipe = new DefaultMetaModelScenarioAffect();
            affEquipe.setName("Produção");
            affEquipe.setExpression(new DefaultNameExpression(
                    "Equipe.Produtividade"));
            connProdByTeam.getAffectList().add(affEquipe);

            sceActTeam.put(connProdByTeam);
            domainModel.putScenario(sceActTeam);
            coding.setScenarioConnection("Produção Baseada na Equipe",
                    "AAtividade");
            design.setScenarioConnection("Produção Baseada na Equipe",
                    "AAtividade");



            MetaModelScenario sceActPreced = new DefaultMetaModelScenario();
            sceActPreced.setName("Precedência de Atividades");
            sceActPreced.setMetaModel(domainModel);

            MetaModelScenarioConnection connTaskPred = new DefaultMetaModelScenarioConnection();
            connTaskPred.setName("AAtividade");
            connTaskPred.setClassName("Atividade");
            MetaModelScenarioAffect affProduction = new DefaultMetaModelScenarioAffect();
            affProduction.setName("Trabalho");
            Expression expIf = new DefaultExpression(NumberOperator.IF);
            Expression expGt = new DefaultExpression(BooleanOperator.LOWERTHAN);
            Expression expGroup = new DefaultExpression(NumberOperator.GROUPSUM);
            expGroup.setLeftOperand(new DefaultNameExpression("Precedente"));
            expGroup.setRightOperand(new DefaultNameExpression("Tempo para Concluir"));
            expGt.setLeftOperand(expGroup);
            expGt.setRightOperand(new DefaultNumberConstantExpression(0.041));
            expIf.setLeftOperand(expGt);
            expIf.setMiddleOperand(new DefaultNameExpression("Trabalho"));
            expIf.setRightOperand(new DefaultNumberConstantExpression(0.0));
            affProduction.setExpression(expIf);
            connTaskPred.getAffectList().add(affProduction);

            sceActPreced.put(connTaskPred);
            domainModel.putScenario(sceActPreced);
            coding.setScenarioConnection("Precedência de Atividades",
                    "AAtividade");
            design.setScenarioConnection("Precedência de Atividades",
                    "AAtividade");



            simulation.setProfile(profile);
            simulation.setMethod(method);
            data
                    .addAll(((JynaSimulableModel) instanceModel)
                    .getAllJynaValued());
            simulation.setSimulationData(data);
            simulation.setModel((JynaSimulableModel) instanceModel);
            simulation.reset();
            data.register(0.0);
            simulation.run();
            // data.register(36.0);
            System.out.println(data);
        } catch (Exception e) {
            System.err.println(e.getCause());
            e.printStackTrace();
        }



    }

    private static void runSimulation(JynaSimulation simulation, int skip) throws Exception {
        //simulation.run();
        int steps = simulation.getProfile().getTimeSteps();
        

        System.out.println("Simulating with " + simulation.getProfile().getTimeSteps() + " iterations. Interval " + simulation.getProfile().getTimeInterval() + " to " + simulation.getProfile().getFinalTime());
        for (int i = 0;i < steps;i++) {
            simulation.step();
            if (i % skip == 0) {
                simulation.register();
            }
        }
        //System.out.println("Simulating done!");
    }
}