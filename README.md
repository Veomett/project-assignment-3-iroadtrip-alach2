# CS 245 (Fall 2023) - Assignment 3 - IRoadTrip

Can you plan the path for a road trip from one country to another?

Change the java source code, but do not change the data files. See Canvas for assignment details.

The program first creates a graph of all the edges that borders each other with an initial weight of zero. It then creates a map of all the country codes with corresponding country names. I alter a few of the country names to follow one standard format to deal with any name refrencing complications. I lastly edit all of the graph edges to contain the correct distance between each of their capitals to be printed and called in the getDistance method. In the getDistance method, I simply check if they are borders, if they are not then it returns -1, if they are it returns their distance so the weight of the edge. In the findPath method, I struggled with getting it to correctly find the best route, but I ended up with implementing a bfs to find the best route out of the vertices in the graph. If there is no connection between the two countries, it returns an empty list. 
