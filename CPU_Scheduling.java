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

	public static void roundRobin(PrintStream output, int[][] procInfo, int timeQuantum)
	{
		int[] procWait = new int[procInfo.length];
		boolean allDone = false;
		int procPointer = 0, timer = 0, numDone = 0, totalWait = 0;
		System.out.println("RR " + timeQuantum);
		while(!allDone)
		{
			for (int proc = 0; proc < procInfo.length; proc++)
			{
				if (!procInfo[proc][2]==0 && procInfo[proc][1]<=timer)
				{
					// Print out the time and the process which gets the CPU
					System.out.printf("%d %d\n", timer, procInfo[proc][0]);
					// Calculate the CPU burst
					// burst = min(proc's remaining burst, timeQuantum)
					int timeIncrement = (procInfo[proc][2] < timeQuantum) ? procInfo[proc][2]:timeQuantum;
					// Increment the timer by the burst amount
					timer += timeIncrement;
					// Decrease process' remaining burst amount by the time increment
					procInfo[proc][2] -= timeIncrement;
					for (int wait = 0; wait < procInfo.length; wait++)
					{
						// If the process doesn't have the CPU, increment its wait time
						if (wait!=proc) procWait[wait]+=timeIncrement;
					}
					// If process is done and all other processes done, end the loop
					if (procInfo[proc][2]==0 && ++numDone==procInfo.length) 
					{
						allDone = true;
					}
				}
			}
		}
		// Calculate the total/average waiting time
		for (int proc = 0; proc < procInfo.length; proc++)
		{
			totalWait+=procWait[proc];
		}
		System.out.printf("Average Wait: %.2f\n", (double)totalWait/(procWait.length));
	}

	public static void shortestJobFirst(PrintStream output, int[][] procInfo)
	{
		int numDone = 0, timer = 0, currProc = 0, nextArrival = 0, timeIncrement = 0, totalWait = 0;
		// Sort array of processes by arrival time
		sort(procInfo, 1);
		System.out.println("SJF");
		// Increment the timer until a process arrives
		while (procInfo[0][1]!=timer) timer++;
		do
		{
			timeIncrement = 0;
			// Cycle through arrived processes
			for (int proc = 0; proc < procInfo.length && procInfo[proc][1]<=timer; proc++)
			{
				// If the process has a shorter burst than the current process, it becomes the current process
				// Same is true if the current process is done (burst of 0)
				if ((procInfo[currProc][2]>procInfo[proc][2] || procInfo[currProc][2]==0) && procInfo[proc][2]!=0)
				{
					currProc = proc;
				}
				if (proc>=nextArrival) nextArrival++;
			}
			// If all processes have arrived or the next arrival after the current time plus the currProc cpu burst, finish currProc
			if (nextArrival==procInfo.length || (procInfo[currProc][2]+timer)<=procInfo[nextArrival][1])
			{
				timeIncrement = procInfo[currProc][2];
				procInfo[currProc][2] = 0;
				numDone++;
			}
			// Else the next arrival occurs before the end of the current processes' CPU burst triggering scheduling
			// Or there is no process that needs CPU but another one is due to arrive
			else{
				timeIncrement = procInfo[nextArrival][1] - timer;
				if (procInfo[currProc][2]!=0)  procInfo[currProc][2] -= timeIncrement;
			}
			timer += timeIncrement;
			for (int waitingProc = 0; waitingProc<nextArrival; waitingProc++)
			{
				if (procInfo[waitingProc][2]!=0 && waitingProc!=currProc)
				{
					totalWait += timeIncrement;
				}
			}
		}while (numDone!=procInfo.length);
		System.out.printf("Average Wait: %.2f\n", (double)totalWait/(procInfo.length));
		
	}

	public static void priorityNoPreempt(PrintStream output, int[][] procInfo)
	{

	}

	public static void priorityWithPreempt(PrintStream output, int[][] procInfo)
	{

	}
	
	// Insertion sort
	public static void sort(int[][] procInfo, int sortCategory)
	{
		for (int proc = 1; proc < procInfo.length; proc++)
		{
			int[] temp = procInfo[proc];
			int j = proc;
			while(j>0 && procInfo[j][sortCategory] < procInfo[j-1][sortCategory])
			{
				procInfo[j] = procInfo[j-1];
				j--;
			}
			procInfo[j] = temp;
		}
	}

}