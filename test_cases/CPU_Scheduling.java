import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;

public class CPU_Scheduling
{
	public static final String INPUT = "input16.txt";
	public static final String OUTPUT = "output.txt";

	public static void main(String[] args) throws FileNotFoundException
	{
		Scanner input = getInput();
		PrintStream output = new PrintStream(new File(OUTPUT));
		String type;
		int timeQuantum = 0;
		System.out.println("Output");
		type = input.next();
		if (type.equals("RR")) timeQuantum = input.nextInt();
		input.nextLine();
		int procNum = input.nextInt();
		input.nextLine();
		// Two dimensional array to hold all of the information about processes
		int[][] procInfo = getInfo(procNum, input);
		switch (type)
		{
			case "RR": roundRobin(output, procInfo, timeQuantum);
				break;
			case "SJF": shortestJobFirst(output, procInfo);
				break;
			case "PR_noPREMP": priorityNoPreempt(output, procInfo);
				break;
			case "PR_withPREMP": priorityWithPreempt(output, procInfo);
				break;
			default: System.out.println("Error with input type: " + type + "\nNot a valid type");
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
		int[][] readyQueue = new int[procInfo.length][4];
		int nextArrival = 0, timer = 0, numDone = 0, totalWait = 0, timeIncrement = 0, readyQueuePointer = 0;
		sort(procInfo,1);
		System.out.println("RR " + timeQuantum);
		// Adding arriving processes to the active process unsorted array
		for (int arriving = nextArrival; arriving < procInfo.length && procInfo[arriving][1]<=timer; arriving++)
		{
			readyQueue[readyQueuePointer++] = procInfo[arriving];
			nextArrival++;
		}
		while(numDone != procInfo.length)
		{
			timeIncrement = 0;
			// Print out the time and the process which gets the CPU
			System.out.printf("%d %d\n", timer, readyQueue[0][0]);
			// Calculate the CPU burst
			// burst = min(proc's remaining burst, timeQuantum)
			timeIncrement = (readyQueue[0][2] < timeQuantum) ? readyQueue[0][2]:timeQuantum;
			// Increment the timer by the burst amount
			timer += timeIncrement;
			// Decrease process' remaining burst amount by the time increment
			readyQueue[0][2] -= timeIncrement;
			totalWait += timeIncrement*(readyQueuePointer-1);
			int[] temp = readyQueue[0];
			readyQueuePointer--;
			for (int proc = 0; proc < readyQueuePointer; proc++)
			{
				readyQueue[proc] = readyQueue[proc+1];
			}
			for (int arriving = nextArrival; arriving < procInfo.length && procInfo[arriving][1]<=timer; arriving++)
			{
				readyQueue[readyQueuePointer++] = procInfo[arriving];
				nextArrival++;
				totalWait += timer - procInfo[arriving][1];
			}
			if (temp[2]!=0) readyQueue[readyQueuePointer++] = temp;
			else numDone++;
		}
		// Calculate the total/average waiting time
		System.out.printf("Average Wait: %.2f\n", (double)totalWait/(procInfo.length));
	}
	
	// Shortest Job First with no preemption
	public static void shortestJobFirst(PrintStream output, int[][] procInfo)
	{
		int[][] readyQueue = new int[procInfo.length][4];
		int numDone = 0, timer = 0, currProc = 0, nextArrival = 0, timeIncrement = 0, totalWait = 0, readyQueuePointer = 0;
		// Sort array of processes by arrival time
		sort(procInfo, 1);
		System.out.println("SJF");
		// Increment the timer until a process arrives
		while (procInfo[0][1]!=timer) timer++;
		// Adding arriving processes to the active process unsorted array
		for (int arriving = nextArrival; arriving < procInfo.length && procInfo[arriving][1]<=timer; arriving++)
		{
			readyQueue[readyQueuePointer++] = procInfo[arriving];
			nextArrival++;
		}
		do
		{
			timeIncrement = 0;
			// Cycle through arrived processes
			// Because the processes are already organized by arrival times, we are guaranteed FCFS for equal CPU bursts
			currProc = 0;
			for (int proc = 1; proc < readyQueuePointer; proc++)
			{
				// Find the process with shortest burst, ties broken by arrival time
				if (readyQueue[currProc][2]>readyQueue[proc][2] || (readyQueue[currProc][2]==readyQueue[proc][2] && readyQueue[currProc][1]>readyQueue[proc][1]))
				{
					currProc = proc;
				}
			}		
			System.out.printf("%d %d\n", timer, readyQueue[currProc][0]);
			timeIncrement = readyQueue[currProc][2];
			readyQueue[currProc][2] = 0;
			numDone++;
			timer += timeIncrement;
			// Remove currProc from readyQueue
			readyQueue[currProc] = readyQueue[readyQueuePointer-1];
			readyQueuePointer--;
			totalWait += timeIncrement*readyQueuePointer;
			// Adding new arrivals to the readyQueue and potentially adding wait times to totalWait
			for (int arriving = nextArrival; arriving < procInfo.length && procInfo[arriving][1]<=timer; arriving++)
			{
				readyQueue[readyQueuePointer++] = procInfo[arriving];
				nextArrival++;
				totalWait+= timer - procInfo[arriving][1];
			}
		}while (numDone!=procInfo.length);
		System.out.printf("Average Wait: %.2f\n", (double)totalWait/(procInfo.length));		
	}

	public static void priorityNoPreempt(PrintStream output, int[][] procInfo)
	{
		int[][] readyQueue = new int[procInfo.length][4];
		int numDone = 0, timer = 0, currProc = 0, nextArrival = 0, timeIncrement = 0, totalWait = 0, readyQueuePointer = 0;
		// Sort array of processes by arrival time
		sort(procInfo, 1);
		System.out.println("PR_noPREMP");
		// Increment the timer until a process arrives
		while (procInfo[0][1]!=timer) timer++;
		// Adding arriving processes to the active process unsorted array
		for (int arriving = nextArrival; arriving < procInfo.length && procInfo[arriving][1]<=timer; arriving++)
		{
			readyQueue[readyQueuePointer++] = procInfo[arriving];
			nextArrival++;
		}
		do
		{
			timeIncrement = 0;
			// Cycle through arrived processes
			currProc = 0;
			for (int proc = 1; proc < readyQueuePointer; proc++)
			{
				// Select the process with highest priority among arrived processes
				if (readyQueue[currProc][3]>readyQueue[proc][3])
				{
					currProc = proc;
				}
			}		
			System.out.printf("%d %d\n", timer, readyQueue[currProc][0]);
			// Incrementing timer and total wait times
			timeIncrement = readyQueue[currProc][2];
			timer += timeIncrement;
			totalWait += (readyQueuePointer-1)*timeIncrement;
			// Removing current process from active process list
			readyQueue[currProc][2] = 0;
			readyQueue[currProc] = readyQueue[readyQueuePointer-1];
			readyQueuePointer--;
			numDone++;
			for (int arriving = nextArrival; arriving < procInfo.length && procInfo[arriving][1]<=timer; arriving++)
			{
				readyQueue[readyQueuePointer++] = procInfo[arriving];
				nextArrival++;
				totalWait+= timer - procInfo[arriving][1];
			}
		}while (numDone!=procInfo.length);
		System.out.printf("Average Wait: %.2f\n", (double)totalWait/(procInfo.length));	
	}

	public static void priorityWithPreempt(PrintStream output, int[][] procInfo)
	{
		int[][] readyQueue = new int[procInfo.length][4];
		int numDone = 0, timer = 0, currProc = 0, nextArrival = 0, timeIncrement = 0, totalWait = 0, readyQueuePointer = 0;
		boolean needDecision = true;
		// Sort array of processes by arrival time
		sort(procInfo, 1);
		System.out.println("PR_withPREMP");
		// Increment the timer until a process arrives
		while (procInfo[0][1]!=timer) timer++;
		do
		{
			timeIncrement = 0;
			// Adding arriving processes to the active process unsorted array
			for (int arriving = nextArrival; arriving < procInfo.length && procInfo[arriving][1]<=timer; arriving++)
			{
				readyQueue[readyQueuePointer++] = procInfo[arriving];
				if (currProc==-1 || procInfo[arriving][3]<readyQueue[currProc][3]) needDecision = true;
				nextArrival++;
			}
			// If a scheduling decision is needed
			if (needDecision)
			{
				// Cycle through arrived processes to determine the highest priority process
				currProc = 0;
				for (int proc = 1; proc < readyQueuePointer; proc++)
				{
					// Finding the active process with the highest priority (lowest number)
					if (readyQueue[currProc][3]>readyQueue[proc][3] || (readyQueue[currProc][2]==readyQueue[proc][2] && readyQueue[currProc][1]>readyQueue[proc][1]))
					{
						currProc = proc;
					}
				}		
				needDecision = false;
				System.out.printf("%d %d\n", timer, readyQueue[currProc][0]);
			}
			// If all processes have arrived or the next arrival is after the current time plus the currProc cpu burst, finish currProc
			if (nextArrival==procInfo.length || (readyQueue[currProc][2]+timer)<=procInfo[nextArrival][1])
			{
				timeIncrement = readyQueue[currProc][2];
				readyQueue[currProc][2] = 0;
				numDone++;
				// Swap the current process with the process at the end of the queue
				readyQueue[currProc] = readyQueue[readyQueuePointer - 1];
				// Current process was removed from the active queue, so the currProc pointer points to nothing
				currProc = -1;
				readyQueuePointer--;
                needDecision = true;
			}
			// Else the next arrival occurs before the end of the current processes' CPU burst triggering scheduling
			// Or there is no process that needs CPU but another one is due to arrive
			else{
				timeIncrement = procInfo[nextArrival][1] - timer;
				// As long as there is an active process
				if (readyQueuePointer!=0)  readyQueue[currProc][2] -= timeIncrement;
			}
			timer += timeIncrement;
			for (int waitingProc = 0; waitingProc<readyQueuePointer; waitingProc++)
			{
				if (waitingProc!=currProc)
				{
					totalWait += timeIncrement;
				}
			}
		}while (numDone!=procInfo.length);
		System.out.printf("Average Wait: %.2f\n", (double)totalWait/(procInfo.length));	
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
                procInfo[j] = temp;
			}
			
		}
	}

}