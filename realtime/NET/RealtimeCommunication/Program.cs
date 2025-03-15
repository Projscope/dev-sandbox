using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.SignalR.Client;

class Program
{
    static async Task Main(string[] args)
    {
        // Define the SignalR server URL
        var hubConnection = new HubConnectionBuilder()
            .WithUrl("wss://api.dev-sandbox.dev/com-hub")
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

            await hubConnection.InvokeAsync("JoinGroup", "api_\\|^T8.%1n5", "channel_DGDrOB7OfYUJ");
            Console.WriteLine("Joined the 'Developers' group.");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error: {ex.Message}");
        }

        // Send a message to the server
        await hubConnection.InvokeAsync("SendMessage", "Hello from .NET!", "api_\\|^T8.%1n5", "channel_DGDrOB7OfYUJ");

        // Keep the connection alive
        Console.WriteLine("Press any key to exit...");
        Console.ReadKey();

        // Stop connection on exit
        await hubConnection.StopAsync();
    }
}
