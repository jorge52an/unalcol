package sis22017.jorge;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;

import java.util.ArrayDeque;
import java.util.Stack;

public class JorgeAgent implements AgentProgram
{
	private ArrayDeque<Byte> commands;
	private Stack<Position> stack;
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
		this.current = new Position( 0, 0 );
		this.direction = 0;
	}

	private byte rotations( int direction, int movement )
	{
		if( ( direction & 1 ) == 1 )
			return ( byte ) ( ( ( movement - direction ) % 4 ) < 0 ? ( ( ( movement  - direction ) % 4 ) + 4 ):
					( ( movement - direction ) % 4 ) );

		return ( byte ) ( ( movement + direction ) % 4 );
	}

	private byte[] getActionsToGoal( Position goal )
	{
		return new byte[]{};
	}

	private byte action( boolean wallFront, boolean wallRight, boolean wallBack, boolean wallLeft, boolean existsBlock,
						boolean existsMark )
	{
		if( !wallFront )
			this.stack.push( new Position( this.current.getX(), this.current.getY() + 1 ) );
		if( !wallRight )
			this.stack.push( new Position( this.current.getX() + 1, this.current.getY() ) );
		if( !wallBack )
			this.stack.push( new Position( this.current.getX(), this.current.getY() - 1 ) );
		if( !wallLeft )
			this.stack.push( new Position( this.current.getX() - 1, this.current.getY() ) );

		/*Position goal = this.stack.pop();
		byte[] actionsToGoal = this.getActionsToGoal( goal );
		for( int i = 1; i < actionsToGoal.length; i++ )
			commands.add( actionsToGoal[i] );

		return actionsToGoal[0];*/
		return 1;
	}

	@Override
	public Action compute( Percept percept )
	{
		byte action;

		if( commands.isEmpty() )
		{
			boolean wallFront = ( Boolean ) percept.getAttribute( "front" );
			boolean wallRight = ( Boolean ) percept.getAttribute( "right" );
			boolean wallBack = ( Boolean ) percept.getAttribute( "back" );
			boolean wallLeft = ( Boolean ) percept.getAttribute( "left" );

			boolean existsBlock = ( Boolean ) percept.getAttribute( "block" );
			boolean existsMark = ( Boolean ) percept.getAttribute( "mark" );

			action = this.action( wallFront, wallRight, wallBack, wallLeft, existsBlock, existsMark );
		}
		else
			action = this.commands.remove();

		switch( action )
		{
			case 1:
				this.direction = ( byte ) ( ( this.direction + 1 ) % 4 );

				return new Action( "rotate" );
			case 2:
				return new Action( "advance" );
			case 3:
				return new Action( "play" );
		}

		return new Action( "no_op" );
	}

	@Override
	public void init()
	{
		this.resetValues();
	}
}