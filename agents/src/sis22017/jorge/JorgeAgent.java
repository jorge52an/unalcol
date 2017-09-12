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

	private boolean markPath( int oldAgentIndexOne, int oldAgentIndexTwo, int newAgentIndexOne,
							  int newAgentIndexTwo, int lengthOne, int lengthTwo, Board goal )
	{
		if( oldAgentIndexOne == newAgentIndexOne && oldAgentIndexTwo == newAgentIndexTwo )
			return true;
		byte[][] instance = goal.getInstance();
		instance[oldAgentIndexOne][oldAgentIndexTwo] = 25;
		goal.setInstance( instance );

		if( oldAgentIndexOne - 1 >= 0 &&
				( instance[oldAgentIndexOne - 1][oldAgentIndexTwo] == 2 || instance[oldAgentIndexOne - 1][oldAgentIndexTwo] == 4 ||
						( instance[oldAgentIndexOne - 1][oldAgentIndexTwo] >= 5 && instance[oldAgentIndexOne - 1][oldAgentIndexTwo] <= 23 ) ) )
		{
			if( markPath( oldAgentIndexOne - 1, oldAgentIndexTwo, newAgentIndexOne, newAgentIndexTwo, lengthOne, lengthTwo, goal ) )
				return true;
		}
		if( oldAgentIndexTwo + 1 < lengthTwo &&
				( instance[oldAgentIndexOne][oldAgentIndexTwo + 1] == 2 || instance[oldAgentIndexOne][oldAgentIndexTwo + 1] == 4 ||
						( instance[oldAgentIndexOne][oldAgentIndexTwo + 1] >= 5 && instance[oldAgentIndexOne][oldAgentIndexTwo + 1] <= 23 ) ) )
		{
			if( markPath( oldAgentIndexOne, oldAgentIndexTwo + 1, newAgentIndexOne, newAgentIndexTwo, lengthOne, lengthTwo, goal ) )
				return true;
		}
		if( oldAgentIndexOne + 1 < lengthOne &&
				( instance[oldAgentIndexOne + 1][oldAgentIndexTwo] == 2 || instance[oldAgentIndexOne + 1][oldAgentIndexTwo] == 4 ||
						( instance[oldAgentIndexOne + 1][oldAgentIndexTwo] >= 5 && instance[oldAgentIndexOne + 1][oldAgentIndexTwo] <= 23 ) ) )
		{
			if( markPath( oldAgentIndexOne + 1, oldAgentIndexTwo, newAgentIndexOne, newAgentIndexTwo, lengthOne, lengthTwo, goal ) )
				return true;
		}
		if( oldAgentIndexTwo - 1 >= 0 && ( instance[oldAgentIndexOne][oldAgentIndexTwo - 1] == 2 || instance[oldAgentIndexOne][oldAgentIndexTwo - 1] == 4 ||
				( instance[oldAgentIndexOne][oldAgentIndexTwo - 1] >= 5 && instance[oldAgentIndexOne][oldAgentIndexTwo - 1] <= 23 ) ) )
		{
			if( markPath( oldAgentIndexOne, oldAgentIndexTwo - 1, newAgentIndexOne, newAgentIndexTwo, lengthOne, lengthTwo, goal ) )
				return true;
		}

		instance = goal.getInstance();
		instance[oldAgentIndexOne][oldAgentIndexTwo] = 4;
		goal.setInstance( instance );

		return false;
	}

	private ArrayList<Byte> getMovementPath( int oldAgentIndexOne, int oldAgentIndexTwo, int newAgentIndexOne,
											 int newAgentIndexTwo, int lengthOne, int lengthTwo, Board goal )
	{
		ArrayList<Byte> movements = new ArrayList<>();
		this.markPath( oldAgentIndexOne, oldAgentIndexTwo, newAgentIndexOne, newAgentIndexTwo, lengthOne, lengthTwo,
				goal );
		int i = oldAgentIndexOne, j = oldAgentIndexTwo;
		while( true )
		{
			if( goal.getInstance()[i][j] >= 5 && goal.getInstance()[i][j] <= 23 )
				break;

			if( i - 1 >= 0 && goal.getInstance()[i - 1][j] == 25 )
			{
				byte[][] instance = goal.getInstance();
				instance[i][j] = 4;
				goal.setInstance( instance );
				movements.add( ( byte ) 0 );
				i--;
			}
			else if( j + 1 < lengthTwo && goal.getInstance()[i][j + 1] == 25 )
			{
				byte[][] instance = goal.getInstance();
				instance[i][j] = 4;
				goal.setInstance( instance );
				movements.add( ( byte ) 1 );
				j++;
			}
			else if( i + 1 < lengthOne && goal.getInstance()[i + 1][j] == 25 )
			{
				byte[][] instance = goal.getInstance();
				instance[i][j] = 4;
				goal.setInstance( instance );
				movements.add( ( byte ) 2 );
				i++;
			}
			else if( j - 1 >= 0 && goal.getInstance()[i][j - 1] == 25 )
			{
				byte[][] instance = goal.getInstance();
				instance[i][j] = 4;
				goal.setInstance( instance );
				movements.add( ( byte ) 3 );
				j--;
			}
			else if( goal.getInstance()[i][j] == 25 )
			{
				if( i - 1 >= 0 && goal.getInstance()[i - 1][j] >= 5 && goal.getInstance()[i - 1][j] <= 23 )
				{
					movements.add( ( byte ) 0 );
					i--;
				}
				else if( j + 1 < lengthTwo && goal.getInstance()[i][j + 1] >= 5 && goal.getInstance()[i][j + 1] <= 23 )
				{
					movements.add( ( byte ) 1 );
					j++;
				}
				else if( i + 1 < lengthOne && goal.getInstance()[i + 1][j] >= 5 && goal.getInstance()[i + 1][j] <= 23 )
				{
					movements.add( ( byte ) 2 );
					i++;
				}
				else if( j - 1 >= 0 && goal.getInstance()[i][j - 1] >= 5 && goal.getInstance()[i][j - 1] <= 23 )
				{
					movements.add( ( byte ) 3 );
					j--;
				}
			}
		}

		return movements;
	}

	private Node getNewNode( Node node, Board goal )
	{
		Board current = node.getBoard();
		int oldAgentIndexOne = 0, oldAgentIndexTwo = 0, newAgentIndexOne = 0, newAgentIndexTwo = 0;
		byte oldAgent = 0, newAgent = 0;
		byte[][] aux = new byte[goal.getInstance().length][goal.getInstance()[0].length];
		for( int i = 0; i < current.getInstance().length; i++ )
			for( int j = 0; j < current.getInstance()[i].length; j++ )
			{
				if( current.getInstance()[i][j] >= 5 )
				{
					oldAgentIndexOne = i;
					oldAgentIndexTwo = j;
					oldAgent = current.getInstance()[i][j];
				}
				if( goal.getInstance()[i][j] >= 5 )
				{
					newAgentIndexOne = i;
					newAgentIndexTwo = j;
					newAgent = goal.getInstance()[i][j];
				}
				aux[i][j] = goal.getInstance()[i][j];
			}

		Board goalCopy = new Board( aux );
		ArrayList<Byte> movements = this.getMovementPath( oldAgentIndexOne, oldAgentIndexTwo, newAgentIndexOne,
				newAgentIndexTwo, current.getInstance().length, current.getInstance()[0].length, goalCopy );
		ArrayList<Byte> actions = new ArrayList<>();
		int direction = oldAgent % 5;
		for( int i = 0; i < movements.size(); i++ )
		{
			byte rotations = this.getRotations( direction, movements.get( i ) );
			for( int j = 0; j < rotations; j++ )
			{
				actions.add( ( byte ) 1 );
				direction = ( byte ) ( ( direction + 1 ) % 4 );
			}
			actions.add( ( byte ) 2 );
		}

		return new Node( node, goal, actions.size() + node.getCost(), actions );
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
			{
				if( ( i != oldIndexOne || j != oldIndexTwo ) && ( i != newIndexOne || j != newIndexTwo ) )
					newInstance[i][j] = instance[i][j];
				if( instance[i][j] >= 5 )
				{
					agentIndexOne = i;
					agentIndexTwo = j;
				}
			}

		if( agentIndexOne != newIndexOne || agentIndexTwo != newIndexTwo )
			newInstance[agentIndexOne][agentIndexTwo] = ( byte ) ( newInstance[agentIndexOne][agentIndexTwo] / 5 );
		newInstance[oldIndexOne][oldIndexTwo] = ( byte ) ( newInstance[oldIndexOne][oldIndexTwo] * 5 + side );

		return new Board( newInstance );
	}

	private boolean isCorner( int i, int j, int lengthOne, int lengthTwo )
	{
		return ( i == 0 && j == 0 ) || ( i == 0 && j == lengthTwo - 1 ) || ( i == lengthOne - 1 && j == 0 ) ||
				( i == lengthOne - 1 && j == lengthTwo - 1 );
	}

	private ArrayList<Node> getChildrens( Node node )
	{
		ArrayList<Node> childrens = new ArrayList<>();
		for( int i = 0; i < node.getBoard().getInstance().length; i++ )
			for( int j = 0; j < node.getBoard().getInstance()[i].length; j++ )
				if( node.getBoard().getInstance()[i][j] == 1 )
				{
					if( i - 1 >= 0 && i + 1 < node.getBoard().getInstance().length &&
							!isCorner( i - 1, j, node.getBoard().getInstance().length, node.getBoard().getInstance()[i].length ) &&
							( node.getBoard().getInstance()[i + 1][j] == 2 || node.getBoard().getInstance()[i + 1][j] >= 4 ) )
					{
						if( node.getBoard().getInstance()[i - 1][j] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i - 1, j,
									4, 1, 0 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i - 1][j] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i - 1, j,
									4, 3, 0 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i - 1][j] >= 20 &&
								node.getBoard().getInstance()[i - 1][j] <= 23 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i - 1, j,
									4, 1, 0 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i - 1][j] >= 10 &&
								node.getBoard().getInstance()[i - 1][j] <= 13 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i - 1, j,
									4, 3, 0 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
					}
					if( j + 1 < node.getBoard().getInstance()[i].length && j - 1 >= 0 &&
							!isCorner( i, j + 1, node.getBoard().getInstance().length, node.getBoard().getInstance()[i].length ) &&
							( node.getBoard().getInstance()[i][j - 1] == 2 || node.getBoard().getInstance()[i][j - 1] >= 4 ) )
					{
						if( node.getBoard().getInstance()[i][j + 1] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j + 1,
									4, 1, 1 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i][j + 1] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j + 1,
									4, 3, 1 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i][j + 1] >= 20 &&
								node.getBoard().getInstance()[i][j + 1] <= 23 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j + 1,
									4, 1, 1 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i][j + 1] >= 10 &&
								node.getBoard().getInstance()[i][j + 1] <= 13 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j + 1,
									4, 3, 1 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
					}
					if( i + 1 < node.getBoard().getInstance().length && i - 1 >= 0 &&
							!isCorner( i + 1, j, node.getBoard().getInstance().length, node.getBoard().getInstance()[i].length ) &&
							( node.getBoard().getInstance()[i - 1][j] == 2 || node.getBoard().getInstance()[i - 1][j] >= 4 ) )
					{
						if( node.getBoard().getInstance()[i + 1][j] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i + 1, j,
									4, 1, 2 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i + 1][j] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i + 1, j,
									4, 3, 2 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i + 1][j] >= 20 &&
								node.getBoard().getInstance()[i + 1][j] <= 23 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i + 1, j,
									4, 1, 2 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i + 1][j] >= 10 &&
								node.getBoard().getInstance()[i + 1][j] <= 13 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i + 1, j,
									4, 3, 2 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
					}
					if( j - 1 >= 0 && j + 1 < node.getBoard().getInstance()[i].length &&
							!isCorner( i, j - 1, node.getBoard().getInstance().length, node.getBoard().getInstance()[i].length ) &&
							( node.getBoard().getInstance()[i][j + 1] == 2 || node.getBoard().getInstance()[i][j + 1] >= 4 ) )
					{
						if( node.getBoard().getInstance()[i][j - 1] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j - 1,
									4, 1, 3 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i][j - 1] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j - 1,
									4, 3, 3 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i][j - 1] >= 20 &&
								node.getBoard().getInstance()[i][j - 1] <= 23 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j - 1,
									4, 1, 3 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i][j - 1] >= 10 &&
								node.getBoard().getInstance()[i][j - 1] <= 13 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j - 1,
									4, 3, 3 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
					}
				}
				else if( node.getBoard().getInstance()[i][j] == 3 )
				{
					if( i - 1 >= 0 && i + 1 < node.getBoard().getInstance().length &&
							!isCorner( i - 1, j, node.getBoard().getInstance().length, node.getBoard().getInstance()[i].length ) &&
							( node.getBoard().getInstance()[i + 1][j] == 2 || node.getBoard().getInstance()[i + 1][j] >= 4 ) )
					{
						if( node.getBoard().getInstance()[i - 1][j] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i - 1, j,
									2, 1, 0 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i - 1][j] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i - 1, j,
									2, 3, 0 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i - 1][j] >= 20 &&
								node.getBoard().getInstance()[i - 1][j] <= 23 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i - 1, j,
									2, 1, 0 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i - 1][j] >= 10 &&
								node.getBoard().getInstance()[i - 1][j] <= 13 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i - 1, j,
									2, 3, 0 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
					}
					if( j + 1 < node.getBoard().getInstance()[i].length && j - 1 >= 0 &&
							!isCorner( i, j +1, node.getBoard().getInstance().length, node.getBoard().getInstance()[i].length ) &&
							( node.getBoard().getInstance()[i][j - 1] == 2 || node.getBoard().getInstance()[i][j - 1] >= 4 ) )
					{
						if( node.getBoard().getInstance()[i][j + 1] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j + 1,
									2, 1, 1 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i][j + 1] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j + 1,
									2, 3, 1 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i][j + 1] >= 20 &&
								node.getBoard().getInstance()[i][j + 1] <= 23 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j + 1,
									2, 1, 1 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i][j + 1] >= 10 &&
								node.getBoard().getInstance()[i][j + 1] <= 13 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j + 1,
									2, 3, 1 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
					}
					if( i + 1 < node.getBoard().getInstance().length && i - 1 >= 0 &&
							!isCorner( i + 1, j, node.getBoard().getInstance().length, node.getBoard().getInstance()[i].length ) &&
							( node.getBoard().getInstance()[i - 1][j] == 2 || node.getBoard().getInstance()[i - 1][j] >= 4 ) )
					{
						if( node.getBoard().getInstance()[i + 1][j] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i + 1, j,
									2, 1, 2 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i + 1][j] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i + 1, j,
									2, 3, 2 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i + 1][j] >= 20 &&
								node.getBoard().getInstance()[i + 1][j] <= 23 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i + 1, j,
									2, 1, 2 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i + 1][j] >= 10 &&
								node.getBoard().getInstance()[i + 1][j] <= 13 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i + 1, j,
									2, 3, 2 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
					}
					if( j - 1 >= 0 && j + 1 < node.getBoard().getInstance()[i].length &&
							!isCorner( i, j - 1, node.getBoard().getInstance().length, node.getBoard().getInstance()[i].length ) &&
							( node.getBoard().getInstance()[i][j + 1] == 2 || node.getBoard().getInstance()[i][j + 1] >= 4 ) )
					{
						if( node.getBoard().getInstance()[i][j - 1] == 4 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j - 1,
									2, 1, 3 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i][j - 1] == 2 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j - 1,
									2, 3, 3 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i][j - 1] >= 20 &&
								node.getBoard().getInstance()[i][j - 1] <= 23 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j - 1,
									2, 1, 3 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
						else if( node.getBoard().getInstance()[i][j - 1] >= 10 &&
								node.getBoard().getInstance()[i][j - 1] <= 13 )
						{
							Board newBoard = this.getNewBoard( node.getBoard(), i, j, i, j - 1,
									2, 3, 3 );
							childrens.add( this.getNewNode( node, newBoard ) );
						}
					}
				}

		return childrens;
	}

	private ArrayList<Byte> getActionsToGoalNode( Node root )
	{
		Node goal = null;
		byte[][] instance = root.getBoard().getInstance();
		byte[][] goalInstance = new byte[instance.length][instance[0].length];
		for( int i = 0; i < instance.length; i++ )
			for( int j = 0; j < instance[i].length; j++ )
				if( instance[i][j] == 2 )
					goalInstance[i][j] = 3;
				else if( instance[i][j] == 1 )
					goalInstance[i][j] = 4;
				else if( instance[i][j] >= 5 && instance[i][j] <= 23 )
					goalInstance[i][j] = ( byte ) ( instance[i][j] / 5 );
				else
					goalInstance[i][j] = instance[i][j];
		Board goalBoard = new Board( goalInstance );
		ArrayList<Byte> actions = new ArrayList<>();

		PriorityQueue<Node> queue = new PriorityQueue<>( 1, ( Comparator<Node> ) ( nodeOne, nodeTwo ) -> {
			if( nodeOne.getAllCost() < nodeTwo.getAllCost() )
				return -1;
			if( nodeOne.getAllCost() > nodeTwo.getAllCost() )
				return 1;
			return 0;
		} );
		queue.add( root );
		boolean foundGoal = false;
		while( !foundGoal )
		{
			Node current = queue.remove();
			ArrayList<Node> childrens = this.getChildrens( current );
			queue.addAll( childrens );
			for( int i = 0; i < childrens.size(); i++ )
			{
				instance = childrens.get( i ).getBoard().getInstance();
				byte[][] instanceAux = new byte[instance.length][instance[0].length];
				for( int j = 0; j < instance.length; j++ )
					for( int k = 0; k < instance[j].length; k++ )
						if( instance[j][k] >= 5 && instance[j][k] <= 23 )
							instanceAux[j][k] = ( byte ) ( instance[j][k] / 5 );
						else
							instanceAux[j][k] = instance[j][k];
				Board aux = new Board( instanceAux );

				foundGoal = goalBoard.equals( aux );
				if( foundGoal )
				{
					goal = childrens.get( i );
					break;
				}
			}
		}

		boolean isRoot = false;
		Node current = goal;
		while( !isRoot )
		{
			ArrayList<Byte> actionsAux = current.getActions();
			if( current == goal )
				actions.addAll( actionsAux );
			else
				actions.addAll( 0, actionsAux );
			current = current.getParent();
			isRoot = current.getParent() == null;
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
				Node root = new Node( null, initial, 0, new ArrayList<>() );
				this.resetValues();
				actionsToGoal = this.getActionsToGoalNode( root );
				for( int i = 1; i < actionsToGoal.size(); i++ )
					commands.add( actionsToGoal.get( i ) );

				return 3; //Play
				//return 4;
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