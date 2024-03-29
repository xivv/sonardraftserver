package com.sonardraft.resources;

import com.google.common.io.Resources;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Component public class SonarResource {

    private GraphQL graphQL;

    @Autowired private GraphQLDataFetchers graphQLDataFetchers;

    @PostConstruct
    public void init () throws IOException {

        URL url = Resources.getResource ( "schema.graphqls" );
        String sdl = Resources.toString ( url, StandardCharsets.UTF_8 );
        GraphQLSchema graphQLSchema = buildSchema ( sdl );
        this.graphQL = GraphQL.newGraphQL ( graphQLSchema ).subscriptionExecutionStrategy ( new SubscriptionExecutionStrategy () ).build ();
    }

    private GraphQLSchema buildSchema ( String sdl ) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser ().parse ( sdl );
        RuntimeWiring runtimeWiring = buildWiring ();
        SchemaGenerator schemaGenerator = new SchemaGenerator ();
        return schemaGenerator.makeExecutableSchema ( typeRegistry, runtimeWiring );
    }

    private RuntimeWiring buildWiring () {
        return RuntimeWiring.newRuntimeWiring ().type ( newTypeWiring ( "Query" ).dataFetcher ( "draft", graphQLDataFetchers.getDraft () ) )
                            .type ( newTypeWiring ( "Query" ).dataFetcher ( "isAlive", graphQLDataFetchers.isAlive () ) )
                            .type ( newTypeWiring ( "Query" ).dataFetcher ( "isClientRunning", graphQLDataFetchers.isClientRunning () ) )
                            .type ( newTypeWiring ( "Query" )
                                                    .dataFetcher ( "toggleClientRunning", graphQLDataFetchers.toggleClientRunning () ) )
                            .type ( newTypeWiring ( "Query" )
                                                    .dataFetcher ( "reloadConfiguration", graphQLDataFetchers.reloadConfiguration () ) )
                            .build ();
    }

    @Bean
    public GraphQL graphQL () {
        return graphQL;
    }

}
