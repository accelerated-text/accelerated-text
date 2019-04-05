import { h }            from 'preact';


export default ({ className, justIcon, message }) => {
    const text =        message && message.toString();

    return (
        <a
            className={ className }
            title={ text }
        >
            ℹ️
            { justIcon ? null : [ ' ', text ] }
        </a>
    );
};
