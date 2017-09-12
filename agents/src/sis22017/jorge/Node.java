package sis22017.jorge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Node
{
	private Node parent;
	private Board board;
	private int cost;
	private ArrayList<Byte> actions;

	public Node( Node parent, Board board, int cost, ArrayList<Byte> actions )
	{
		this.parent = parent;
		this.board = board;
		this.cost = cost;
		this.actions = actions;
	}

	private int getHeuristicCost()
	{
		HashSet<Position> marks = new HashSet<>();
		ArrayList<Position> blocks = new ArrayList<>();
		byte[][] instance = this.board.getInstance();
		int count = 0;
		for( int i = 0; i < instance.length; i++ )
			for( int j = 0; j < instance[i].length; j++ )
				if( instance[i][j] == 2 )
					marks.add( new Position( i, j ) );
				else if( instance[i][j] == 1 )
					blocks.add( new Position( i, j ) );

		for( int i = 0; i < blocks.size(); i++ )
		{
			HashMap<Position, Integer> distances = new HashMap<>();
			for( Position mark: marks )
			{
				int distance = Math.abs( mark.getX() - blocks.get( i ).getX() ) + Math.abs( mark.getY() - blocks.get( i ).getY() );
				distances.put( mark, distance );
			}
			double minDistance = Double.POSITIVE_INFINITY;
			Position aux = null;
			for( Position position: distances.keySet() )
			{
				if( distances.get( position ) < minDistance )
				{
					minDistance = distances.get( position );
					aux = position;
				}
			}
			count += minDistance;
			marks.remove( aux );
		}

		return count;
	}

	public Node getParent()
	{
		return parent;
	}

	public Board getBoard()
	{
		return board;
	}

	public int getCost()
	{
		return this.cost;
	}

	public int getAllCost()
	{
		return this.cost + this.getHeuristicCost();
	}

	public ArrayList<Byte> getActions()
	{
		return this.actions;
	}
}