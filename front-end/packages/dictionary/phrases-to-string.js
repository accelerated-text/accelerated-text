export default phrases =>
    phrases.map( phrase => phrase.text )
        .join( ', ' );
