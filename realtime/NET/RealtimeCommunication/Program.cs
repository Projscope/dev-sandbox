using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.SignalR.Client;

class Program
{
    static async Task Main(string[] args)
    {
        // Define the SignalR server URL
        var hubConnection = new HubConnectionBuilder()
            .WithUrl("wss://localhost:44311/com-hub")
            .WithAutomaticReconnect() // Reconnect on disconnect
            .Build();

        // Handle incoming messages
        hubConnection.On<string>("ReceiveMessage", (message) =>
        {
            Console.WriteLine($"{message}");
        });

        try
        {
            // Start the connection
            await hubConnection.StartAsync();
            Console.WriteLine("Connected to SignalR!");

            await hubConnection.InvokeAsync("JoinGroup", "api_3y><pxotI8", "channel_Fa8A3og4IucY");
            Console.WriteLine("Joined the 'Developers' group.");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error: {ex.Message}");
        }

        // Send a message to the server
        await hubConnection.InvokeAsync("SendMessage", "Hello from .NET!", "api_3y><pxotI8", "channel_Fa8A3og4IucY");

        // Keep the connection alive
        Console.WriteLine("Press any key to exit...");
        Console.ReadKey();

        // Stop connection on exit
        await hubConnection.StopAsync();
    }
}
