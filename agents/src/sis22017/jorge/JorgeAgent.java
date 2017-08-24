package sis22017.jorge;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;

import java.util.*;

public class JorgeAgent implements AgentProgram
{
	private ArrayDeque<Byte> commands;
	private Stack<Position> stack;
	private HashSet<Position> visited;
	private Position current;
	private Byte direction;

	public JorgeAgent()
	{
		this.resetValues();
	}

	private void resetValues()
	{
		this.commands = new ArrayDeque<>();
		this.stack = new Stack<>();
		this.visited = new HashSet<>();
		this.current = new Position( 0, 0 );
		this.visited.add( this.current );
		this.direction = 0;
	}

	private int module( int value, int divider )
	{
		if( value >= 0 )
			return value % divider;

		return ( value % divider ) + divider;
	}

	private byte rotations( int direction, int movement )
	{
		if( ( direction & 1 ) == 1 )
			return ( byte ) this.module( movement - direction, 4 );

		return ( byte ) this.module( movement + direction, 4 );
	}

	private boolean[] getWalls( boolean[] walls )
	{
		boolean[] newValues = new boolean[4];

		for( int i = 0; i < walls.length; i++ )
			newValues[this.module( i + this.direction, 4 )] = walls[i];

		return newValues;
	}

	private ArrayList<Byte> getActionsToGoal( Position goal )
	{
		ArrayList<Byte> actions = new ArrayList<>();

		if( goal.getX() == this.current.getX() || goal.getY() == this.current.getY() )
		{
			byte rotations = 0;
			if( goal.getX() == this.current.getX() )
				if( goal.getY() == this.current.getY() + 1 )
					rotations = this.rotations( this.direction, 0 );
				else
					rotations = this.rotations( this.direction, 2 );
			else
				if( goal.getX() == this.current.getX() + 1 )
					rotations = this.rotations( this.direction, 1 );
				else
					rotations = this.rotations( this.direction, 3 );

			for( int i = 0; i < rotations; i++ )
				actions.add( ( byte ) 1 );
			actions.add( ( byte ) 2 );
		}

		return actions;
	}

	private byte getAction( boolean[] walls, boolean existsBlock, boolean existsMark )
	{
		boolean visitNewPosition = false;

		if( !walls[0] )
		{
			Position newPosition = new Position( this.current.getX(), this.current.getY() + 1 );
			if( !this.visited.contains( newPosition ) )
			{
				this.stack.push( newPosition );
				visitNewPosition = true;
			}
		}
		if( !walls[1] )
		{
			Position newPosition = new Position( this.current.getX() + 1, this.current.getY() );
			if( !this.visited.contains( newPosition ) )
			{
				this.stack.push( newPosition );
				visitNewPosition = true;
			}
		}
		if( !walls[2] )
		{
			Position newPosition = new Position( this.current.getX(), this.current.getY() - 1 );
			if( !this.visited.contains( newPosition ) )
			{
				this.stack.push( newPosition );
				visitNewPosition = true;
			}
		}
		if( !walls[3] )
		{
			Position newPosition = new Position( this.current.getX() - 1, this.current.getY() );
			if( !this.visited.contains( newPosition ) )
			{
				this.stack.push( newPosition );
				visitNewPosition = true;
			}
		}

		if( !visitNewPosition )
			return 4; //Play

		Position goal = this.stack.pop();
		ArrayList<Byte> actionsToGoal = this.getActionsToGoal( goal );
		for( int i = 1; i < actionsToGoal.size(); i++ )
			commands.add( actionsToGoal.get( i ) );

		return actionsToGoal.get( 0 );
	}

	@Override
	public Action compute( Percept percept )
	{
		byte action;

		if( commands.isEmpty() )
		{
			boolean[] walls = new boolean[4];
			walls[0] = ( Boolean ) percept.getAttribute( "front" );
			walls[1] = ( Boolean ) percept.getAttribute( "right" );
			walls[2] = ( Boolean ) percept.getAttribute( "back" );
			walls[3] = ( Boolean ) percept.getAttribute( "left" );

			boolean existsBlock = ( Boolean ) percept.getAttribute( "block" );
			boolean existsMark = ( Boolean ) percept.getAttribute( "mark" );

			walls = this.getWalls( walls );

			action = this.getAction( walls, existsBlock, existsMark );
		}
		else
			action = this.commands.remove();

		switch( action )
		{
			case 1:
				this.direction = ( byte ) ( ( this.direction + 1 ) % 4 );

				return new Action( "rotate" );
			case 2:
				switch( this.direction )
				{
					case 0:
						this.current = new Position( this.current.getX(), this.current.getY() + 1 );
						break;
					case 1:
						this.current = new Position( this.current.getX() + 1, this.current.getY() );
						break;
					case 2:
						this.current = new Position( this.current.getX(), this.current.getY() - 1 );
						break;
					case 3:
						this.current = new Position( this.current.getX() - 1, this.current.getY() );
						break;
				}
				this.visited.add( this.current );

				return new Action( "advance" );
			case 3:
				return new Action( "play" );
			case 4:
				return new Action( "no_op" );
		}

		return new Action( "no_op" );
	}

	@Override
	public void init()
	{
		this.resetValues();
	}
}