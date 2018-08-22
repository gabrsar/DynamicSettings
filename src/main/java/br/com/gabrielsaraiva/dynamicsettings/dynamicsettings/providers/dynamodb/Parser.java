package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.dynamodb;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

interface Parser {
    Object parse(AttributeValue v);
}
