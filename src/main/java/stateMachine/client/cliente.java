package stateMachine.client;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.server.StateMachine;
import stateMachine.commands.Delete;
import stateMachine.commands.Get;
import stateMachine.commands.Put;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class cliente  extends StateMachine {

    public static void main( String[] args ) {
        List<Address> addresses = new LinkedList<>();

        CopycatClient.Builder builder = CopycatClient.builder()
                .withTransport( NettyTransport.builder()
                        .withThreads(4)
                        .build());

        CopycatClient client = builder.build();

        for(int i = 0; i <args.length;i+=2)
        {
            Address address = new Address(args[i], Integer.parseInt(args[i+1]));
            addresses.add(address);
        }

        CompletableFuture<CopycatClient> future = client.connect(addresses);
        future.join();

        CompletableFuture[] futures = new CompletableFuture[]{
                client.submit(new Put("foo", "Hello world!")),
                client.submit(new Get("foo")).thenAccept(result -> System.out.println("foo is: " + result)),
                client.submit(new Delete("foo")).thenRun(() -> System.out.println("foo has been deleted")),
        };

        CompletableFuture.allOf(futures).thenRun(() -> System.out.println("Commands completed!"));

    }
}
