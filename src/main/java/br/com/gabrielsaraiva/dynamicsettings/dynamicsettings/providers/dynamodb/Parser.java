package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.dynamodb;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

/** This interface is used to convert DynamoDB types into something usable by Java easily */
interface Parser {
    Object parse(AttributeValue v);
}
