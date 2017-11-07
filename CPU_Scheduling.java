import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CPU_Scheduling
{
	public static final String INPUT = "input.txt";
	public static final String OUTPUT = "output.txt";

	public static void main(String[] args) throws FileNotFoundException
	{
		Scanner input = getInput();
		PrintStream output = new PrintStream(new File(OUTPUT));
		String type;
		int timeQuantum = 0, outputNum = 0;
		// Loop until there are no more runs in the input file
		// Each loop will execute one CPU scheduling run
		while (input.hasNextLine())
		{
			System.out.println("Output " + (++outputNum));
			type = input.next();
			if (type.equals("RR")) timeQuantum = input.nextInt();
			input.nextLine();
			int procNum = input.nextInt();
			input.nextLine();
			// Two dimensional array to hold all of the information about processes
			int[][] procInfo = getInfo(procNum, input);
			switch (type)
			{
				case "RR": roundRobin(input, output, procInfo, timeQuantum);
					break;
				case "SJF": shortestJobFirst(input, output, procInfo);
					break;
				case "PR_noPREMP": priorityNoPreempt(input, output, procInfo);
					break;
				case "PR_withPREMP": priorityWithPreempt(input, output, procInfo);
					break;
				default: System.out.println("Error with input type: " + type + "\nNot a valid type");
			}
		}
	}


	// Get a Scanner for the input file
	public static Scanner getInput() throws FileNotFoundException
	{
		File f = new File(INPUT);
		return new Scanner(f);
	}

	public static int[][] getInfo(int procNum, Scanner input)
	{
		int[][] procInfo = new int[procNum][4];
		for (int proc = 0; proc<procNum; proc++)
		{
			// Get process number
			procInfo[proc][0] = input.nextInt();
			// Get Arrival Time
			procInfo[proc][1] = input.nextInt();
			// Get CPU burst
			procInfo[proc][2] = input.nextInt();
			// Get priority
			procInfo[proc][3] = input.nextInt();
			if (proc!=procNum-1) input.nextLine();
		}
		return procInfo;
	}

	public static void roundRobin(Scanner input, PrintStream output, int[][] procInfo, int timeQuantum)
	{
		boolean[] procDone = new boolean[procInfo.length];
		boolean allDone = false;
		int procPointer = 0, timer = 0, numDone = 0;
		System.out.println("RR " + timeQuantum);
		while(!allDone)
		{
			for (int proc = 0; proc < procInfo.length; proc++)
			{
				if (!procDone[proc] && procInfo[proc][1]<=timer)
				{
					System.out.printf("%d %d\n", timer, procInfo[proc][0]);
					int timeIncrement = (procInfo[proc][2] < timeQuantum) ? procInfo[proc][2]:timeQuantum;
					timer += timeIncrement;
					procInfo[proc][2] -= timeIncrement;
					if (procInfo[proc][2]==0) 
					{
						procDone[proc] = true;
						if(++numDone==procInfo.length) allDone = true;
					}
				}
			}
		}
	}

	public static void shortestJobFirst(Scanner input, PrintStream output, int[][] procInfo)
	{

	}

	public static void priorityNoPreempt(Scanner input, PrintStream output, int[][] procInfo)
	{

	}

	public static void priorityWithPreempt(Scanner input, PrintStream output, int[][] procInfo)
	{

	}


}