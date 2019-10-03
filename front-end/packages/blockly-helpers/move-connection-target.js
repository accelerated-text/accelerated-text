export default ( fromConn, toConn ) => (
    fromConn
    && fromConn.targetConnection
    && toConn
    && toConn.isConnectionAllowed( fromConn.targetConnection )
    && toConn.connect( fromConn.targetConnection )
);

