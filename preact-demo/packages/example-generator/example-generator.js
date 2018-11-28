const tokenToString = token =>
    ( !token )
        ? ''
    : ( token instanceof Array )
        ? token.map( tokenToString ).join( ' ' )
    : token.content;

export default async tokens => tokenToString( tokens );
