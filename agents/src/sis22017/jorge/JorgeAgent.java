package sis22017.jorge;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;

import java.util.*;

public class JorgeAgent implements AgentProgram
{
	private ArrayDeque<Byte> commands;

	//Scan
	private Stack<Position> stack;
	private HashSet<Position> blocks;
	private HashSet<Position> marks;
	private HashSet<Position> visited;
	private Stack<Position> path;
	private Position current;
	private Byte direction;
	private Byte minX;
	private Byte minY;
	private Byte maxX;
	private Byte maxY;

	public JorgeAgent()
	{
		this.resetValues();
	}

	private void resetValues()
	{
		this.commands = new ArrayDeque<>();
		this.stack = new Stack<>();
		this.blocks = new HashSet<>();
		this.marks = new HashSet<>();
		this.visited = new HashSet<>();
		this.path = new Stack<>();
		this.current = new Position( 0, 0 );
		this.visited.add( this.current );
		this.path.add( this.current );
		this.direction = 0;
		this.minX = 0;
		this.minY = 0;
		this.maxX = 0;
		this.maxY = 0;
	}

	private int module( int value, int divider )
	{
		if( value >= 0 )
			return value % divider;

		return ( value % divider ) + divider;
	}

	private boolean[] getWalls( boolean[] walls )
	{
		boolean[] newValues = new boolean[4];

		for( int i = 0; i < walls.length; i++ )
			newValues[this.module( i + this.direction, 4 )] = walls[i];

		return newValues;
	}

	private byte getRotations( int direction, int movement )
	{
		if( ( direction & 1 ) == 1 )
			return ( byte ) this.module( movement - direction, 4 );

		return ( byte ) this.module( movement + direction, 4 );
	}

	private boolean neighbor( Position current, Position goal )
	{
		return ( goal.getX() == current.getX() && Math.abs( goal.getY() - current.getY() ) == 1 ) ||
				( goal.getY() == current.getY() && Math.abs( goal.getX() - current.getX() ) == 1 );
	}

	private byte getRotationsByNextPosition( Position current, Position goal, byte direction )
	{
		byte rotations;
		if( goal.getX() == current.getX() )
			if( goal.getY() == current.getY() + 1 )
				rotations = this.getRotations( direction, 0 );
			else
				rotations = this.getRotations( direction, 2 );
		else
			if( goal.getX() == current.getX() + 1 )
				rotations = this.getRotations( direction, 1 );
			else
				rotations = this.getRotations( direction, 3 );

		return rotations;
	}

	private ArrayList<Byte> getActionsToGoal( Position goal )
	{
		ArrayList<Byte> actions = new ArrayList<>();

		if( neighbor( this.current, goal ) )
		{
			byte rotations = this.getRotationsByNextPosition( this.current, goal, this.direction );

			for( int i = 0; i < rotations; i++ )
				actions.add( ( byte ) 1 );
			actions.add( ( byte ) 2 );
		}
		else
		{
			Stack<Position> pathAux = new Stack<>();
			Position current = this.path.pop();
			pathAux.push( current );
			byte direction = this.direction;
			byte rotations;
			while( true )
			{
				Position next = this.path.pop();
				pathAux.push( next );

				rotations = this.getRotationsByNextPosition( current, next, direction );
				for( int i = 0; i < rotations; i++ )
				{
					actions.add( ( byte ) 1 );
					direction = ( byte ) ( ( direction + 1 ) % 4 );
				}
				actions.add( ( byte ) 2 );

				current = next;
				if( this.neighbor( current, goal ) )
					break;
			}

			while( !pathAux.isEmpty() )
				this.path.push( pathAux.pop() );

			rotations = this.getRotationsByNextPosition( current, goal, direction );

			for( int i = 0; i < rotations; i++ )
			{
				actions.add( ( byte ) 1 );
				direction = ( byte ) ( ( direction + 1 ) % 4 );
			}
			actions.add( ( byte ) 2 );
		}

		return actions;
	}

	private int[] getIndexes( int x, int y )
	{
		return new int[]{ this.maxY - y, x - this.minX };
	}

	private int getCost( Board current, Board goal, byte position )
	{


		return 0;
	}

	private Board getNewBoard( Board board, int oldIndexOne, int oldIndexTwo, int newIndexOne, int newIndexTwo,
							   int newValueOne, int newValueTwo, int side )
	{
		byte[][] instance = board.getInstance();
		byte[][] newInstance = new byte[instance.length][instance[0].length];
		int agentIndexOne = 0, agentIndexTwo = 0;

		newInstance[oldIndexOne][oldIndexTwo] = ( byte ) newValueOne;
		newInstance[newIndexOne][newIndexTwo] = ( byte ) newValueTwo;

		for( int i = 0; i < instance.length; i++ )
			for( int j = 0; j < instance[i].length; j++ )
				if( ( i != oldIndexOne || j != oldIndexTwo ) && ( i != newIndexOne || j != newIndexTwo ) )
				{
					newInstance[i][j] = instance[i][j];
					if( instance[i][j] >= 5 )
					{
						agentIndexOne = i;
						agentIndexTwo = j;
					}
				}

		newInstance[agentIndexOne][agentIndexTwo] = ( byte ) ( newInstance[agentIndexOne][agentIndexOne] / 5 );
		newInstance[oldIndexOne][oldIndexTwo] = ( byte ) ( newInstance[oldIndexOne][oldIndexTwo] * 5 + side );

		return new Board( newInstance );
	}

	private ArrayList<Node> getChildrens( Node node )
	{
		ArrayList<Node> childrens = new ArrayList<>();
		for( int i = 0; i < node.getBoard().getInstance().length; i++ )
			for( int j = 0; j < node.getBoard().getInstance()[i].length; j++ )
				if( node.getBoard().getInstance()[i][j] == 1 )
				{
					if( i - 1 >= 0 )
					{
						if( node.getBoard().getInstance()[i - 1][j] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i - 1, j,
									4, 1, 0 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
						else if( node.getBoard().getInstance()[i - 1][j] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i - 1, j,
									4, 3, 0 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
					}
					if( j + 1 < node.getBoard().getInstance()[i].length )
					{
						if( node.getBoard().getInstance()[i][j + 1] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j + 1,
									4, 1, 1 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
						else if( node.getBoard().getInstance()[i][j + 1] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j + 1,
									4, 3, 1 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
					}
					if( i + 1 < node.getBoard().getInstance().length )
					{
						if( node.getBoard().getInstance()[i + 1][j] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i + 1, j,
									4, 1, 2 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
						else if( node.getBoard().getInstance()[i + 1][j] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i + 1, j,
									4, 3, 2 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
					}
					if( j - 1 >= 0 )
					{
						if( node.getBoard().getInstance()[i][j - 1] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j - 1,
									4, 1, 3 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
						else if( node.getBoard().getInstance()[i][j - 1] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j - 1,
									4, 3, 3 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
					}
				}
				else if( node.getBoard().getInstance()[i][j] == 3 )
				{
					if( i - 1 >= 0 )
					{
						if( node.getBoard().getInstance()[i - 1][j] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i - 1, j,
									2, 1, 0 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
						else if( node.getBoard().getInstance()[i - 1][j] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i - 1, j,
									2, 3, 0 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
					}
					if( j + 1 < node.getBoard().getInstance()[i].length )
					{
						if( node.getBoard().getInstance()[i][j + 1] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j + 1,
									2, 1, 1 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
						else if( node.getBoard().getInstance()[i][j + 1] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j + 1,
									2, 3, 1 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
					}
					if( i + 1 < node.getBoard().getInstance().length )
					{
						if( node.getBoard().getInstance()[i + 1][j] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i + 1, j,
									2, 1, 2 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
						else if( node.getBoard().getInstance()[i + 1][j] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i + 1, j,
									2, 3, 2 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
					}
					if( j - 1 >= 0 )
					{
						if( node.getBoard().getInstance()[i][j - 1] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j - 1,
									2, 1, 3 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
						else if( node.getBoard().getInstance()[i][j - 1] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j - 1,
									2, 3, 3 );
							childrens.add( new Node( node, newBoard, 0 ) );
						}
					}
				}

		return childrens;
	}

	private ArrayList<Byte> getActionsToGoalNode( Node root )
	{
		ArrayList<Node> childrens = this.getChildrens( root );

		return new ArrayList<>();
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

		if( existsBlock && !this.blocks.contains( this.current ) )
			this.blocks.add( this.current );
		if( existsMark && !this.marks.contains( this.current ) )
			this.marks.add( this.current );

		Position goal = null;
		ArrayList<Byte> actionsToGoal;
		if( visitNewPosition )
		{
			goal = this.stack.pop();
			actionsToGoal = this.getActionsToGoal( goal );
		}
		else
		{
			boolean allVisited = true;
			while( true )
			{
				if( this.stack.empty() )
					break;

				goal = this.stack.pop();
				if( !this.visited.contains( goal ) )
				{
					allVisited = false;
					break;
				}
			}

			if( allVisited )
			{
				byte[][] instance = new byte[( this.maxY - this.minY ) + 1][( this.maxX - this.minX ) + 1];
				for( Position position: this.blocks )
				{
					int[] indexes = getIndexes( position.getX(), position.getY() );
					instance[indexes[0]][indexes[1]] = 1;
				}
				for( Position position: this.marks )
				{
					int[] indexes = getIndexes( position.getX(), position.getY() );
					instance[indexes[0]][indexes[1]] += 2;
				}
				for( Position position: this.visited )
				{
					int[] indexes = getIndexes( position.getX(), position.getY() );
					if( instance[indexes[0]][indexes[1]] == 0 )
						instance[indexes[0]][indexes[1]] = 4;
				}
				int[] indexes = getIndexes( 0, 0 );
				instance[indexes[0]][indexes[1]] = ( byte ) ( 5 * instance[indexes[0]][indexes[1]] );

				Board initial = new Board( instance );
				Node root = new Node( null, initial, 0 );
				this.resetValues();
				this.getActionsToGoalNode( root );

				//return 3; //Play
				return 4;
			}
			else
				actionsToGoal = this.getActionsToGoal( goal );
		}

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
				this.path.push( this.current );

				if( this.current.getX() < this.minX )
					this.minX = this.current.getX();
				if( this.current.getY() < this.minY )
					this.minY = this.current.getY();
				if( this.current.getX() > this.maxX )
					this.maxX = this.current.getX();
				if( this.current.getY() > this.maxY )
					this.maxY = this.current.getY();

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