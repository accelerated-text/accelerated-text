export default event => ([
    event.ctrlKey   && 'Ctrl',
    event.altKey    && 'Alt',
    event.shiftKey  && 'Shift',
    event.metaKey   && 'Meta',
    event.key,
]
    .filter( Boolean )
    .join( '_' )
);
