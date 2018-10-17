/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package muzicmidiapll;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.sound.midi.*;
import javax.swing.*;
/**
 *
 * @author Legion
 */
public class BuildGUID {
    
    JPanel mainPanel;
    ArrayList<JCheckBox> checkboxList;
    Track track;
    JFrame frame;
    Sequencer sequencer;
    Sequence sequence;

    
    String[] instrumentName = {"Bass Drump", "Closed Hit-Ha", "Open Hi-Hat", 
            "Acoustic Player", "Crash Cymbal", "Hand Clap", "Hight Tom", "Hi Bongo",
    "Marakas", "Whistli", "Low Congra", "Cowbell", "Vibrasslap", "Low-mid Tom", 
    "Hight Agog", "Open Hi Congra"};
    int [] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};
    
    public void buildGui(){
        frame = new JFrame("Cyber Beat-Box");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        checkboxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        
        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);
        
        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);
        
        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);
        
        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);
        
        Box  nameBox = new Box(BoxLayout.X_AXIS);
        for (int i = 0; i < 16; i++){
            nameBox.add(new Label(instrumentName[i]));
        }
        background.add(BorderLayout.SOUTH, buttonBox);
        background.add(BorderLayout.NORTH, nameBox);
        
        frame.getContentPane().add(background);
        
        GridLayout grid = new GridLayout(16,16);
        grid.setVgap(1);
        grid.setHgap(2);
        
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel );
        
        for(int i = 0; i < 256; i++){
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }
    
        setUpMidi();
        
        frame.setBounds(50, 50, 300, 300);
        frame.pack();
        frame.setVisible(true);
    }
    public void setUpMidi(){
        try{
          sequencer = MidiSystem.getSequencer();
          sequencer.open();
          sequence = new Sequence(Sequence.PPQ, 4);
          track = sequence.createTrack();
          sequencer.setTempoInBPM(120);
          
          
        }catch(Exception ex){
            ex.printStackTrace();
    }
    }
    public void BuildTrackAndStart() {
        
        int []trackList = null;
        
        sequence.deleteTrack(track);
        track = sequence.createTrack();
        
        for (int i = 0; i < 16; i++){
        trackList = new int[16];
        
        int key = instruments[i];
        
        
                for ( int j = 0; j < 16;  j++){
                    JCheckBox jc =  (JCheckBox) checkboxList.get(j + (16*i));
                    if(jc.isSelected()){
                        trackList[j] = key;
                    }else{
                        trackList[j] = 0;
                    }
                }
        makeTracks(trackList);
        track.add(makeEvent(176,1,127,0,16));
        }
    track.add(makeEvent(192,9,1,0,15));
        try{
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class MyStartListener implements ActionListener{
    public void actionPerformed(ActionEvent a){
        BuildTrackAndStart();
        }
    }
    public class MyStopListener implements ActionListener{
        public void actionPerformed(ActionEvent b){
            sequencer.stop();
        }
    }
    public class MyUpTempoListener implements ActionListener{
        public void actionPerformed(ActionEvent c){
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * 1.03));
        }
    }
    public class MyDownTempoListener implements ActionListener{
        public void actionPerformed(ActionEvent d){
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * .97));
        }
    }
    
    public void makeTracks(int []list ){
        for (int i = 0; i < 16; i++){
            int key = list[i];
            
            if (key != 0){
                track.add(makeEvent(144,9,key,100, i));
                track.add(makeEvent(128, 9,key,100,i + i));
            }
        }
    }   
    public MidiEvent makeEvent(int comd, int chan, int one, int tow, int tick){
        MidiEvent event = null;
        try{
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, one, tow);
            event = new MidiEvent(a, tick);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return event;
    }
} 
   
