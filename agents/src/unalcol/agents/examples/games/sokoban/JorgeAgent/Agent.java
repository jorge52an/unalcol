package unalcol.agents.examples.games.sokoban.JorgeAgent;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;

import java.util.ArrayList;

public class Agent implements AgentProgram
{
	private SimpleLanguage language;
	private ArrayList<String> commands;

	public Agent()
	{
		this.commands = new ArrayList<>();
	}

	public Agent( SimpleLanguage language )
	{
		this.language = language;
		this.commands = new ArrayList<>();
	}

	public void setLanguage( SimpleLanguage language )
	{
		this.language = language;
	}

	@Override
	public Action compute( Percept percept )
	{
		return null;
	}

	@Override
	public void init()
	{
		this.commands.clear();
	}
}