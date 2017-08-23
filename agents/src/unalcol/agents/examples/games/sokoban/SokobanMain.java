package unalcol.agents.examples.games.sokoban;

import sis22017.jorge.JorgeAgent;
import unalcol.agents.Agent;
import unalcol.agents.examples.labyrinth.Labyrinth;
import unalcol.agents.examples.labyrinth.teseo.simple.RandomReflexTeseo;
import unalcol.agents.examples.labyrinth.teseo.simple.TeseoSimple;
import unalcol.agents.simulate.util.InteractiveAgentProgram;
import unalcol.agents.simulate.util.SimpleLanguage;

public class SokobanMain {
    private static SimpleLanguage getLanguage(){
	    return  new SimpleLanguage( new String[]{"front", "right", "back", "left", "block", "mark"},
	                                   new String[]{"no_op", "die", "advance", "rotate", "play"}
	                                   );
	  }

	  public static void main( String[] argv ){
	    //InteractiveAgentProgram p = new InteractiveAgentProgram( getLanguage() );
	    //TeseoSimple p = new TeseoSimple();
	    JorgeAgent p = new JorgeAgent();
	    SokobanBoardDrawer.DRAW_AREA_SIZE = 600;
	    SokobanBoardDrawer.CELL_SIZE = 40;
	    Labyrinth.DEFAULT_SIZE = 15;
	    Agent agent = new Agent( p );
	    SokobanMainFrame frame = new SokobanMainFrame( agent, getLanguage() );
	    frame.setVisible(true); 
	  }
	}