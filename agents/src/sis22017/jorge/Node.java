package sis22017.jorge;

import java.util.ArrayList;

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
		return 0;
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
		return this.cost + this.getHeuristicCost();
	}

	public ArrayList<Byte> getActions()
	{
		return this.actions;
	}
}