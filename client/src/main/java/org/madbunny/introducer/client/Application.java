package org.madbunny.introducer.client;

import org.madbunny.client.TcpClient;
import org.madbunny.introducer.message.MessageWriter;
import org.madbunny.introducer.message.json.JsonSerializer;
import org.madbunny.introducer.message.type.ServerAddress;

public class Application {
    public static void main(String[] arguments) {
        var args = parseArguments(arguments);
        var writer = new MessageWriter(new JsonSerializer());
        try (var client = new TcpClient(args.remoteAddress, args.remotePort)) {
            var message = new ServerAddress("some-host", 123);
            writer.write(client, message);
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private static ProgramArguments parseArguments(String[] args) {
        if (args.length < 2) {
            throw new RuntimeException("Not enough arguments");
        }
        return new ProgramArguments(
                args[0],
                Integer.parseInt(args[1])
        );
    }

    private record ProgramArguments(
            String remoteAddress,
            int remotePort
    ) {}
}
