export default async tokens => tokenToString( tokens );

function tokenToString( token ){

    if( !token ){
        return '';
    } else if( token instanceof Array ){
        return token.map( tokenToString ).join( ' ' );
    } else {
        return token.content;
    }
}
