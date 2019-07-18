export const DELETE_CONFIRM =   'Are you sure you want to delete this item?';

export default ( onConfirm, onDecline, message = DELETE_CONFIRM ) => (
    confirm( message )   // eslint-disable-line no-alert
        ? ( onConfirm && onConfirm())
        : ( onDecline && onDecline())
);
