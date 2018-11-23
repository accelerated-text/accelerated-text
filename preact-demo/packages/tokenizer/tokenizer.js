export default async str =>
    str
        .split( /\s+/ )
        .map( content => ({ type: 'word', content }));

