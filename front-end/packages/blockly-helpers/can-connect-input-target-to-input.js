export default srcInput => targetInput => (
    targetInput
    && ! targetInput.connection.isConnected()
    && targetInput.connection.checkType_(       // eslint-disable-line no-underscore-dangle
        srcInput.connection.targetConnection
    )
);
