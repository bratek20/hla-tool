// DO NOT EDIT! Autogenerated by HLA tool

using B20.Ext;

using OtherModule.Api;
using TypesModule.Api;

namespace SomeModule.Api
{
    public interface SomeEmptyInterface
    {
    }

    public interface SomeInterface
    {
        void someEmptyMethod();

        /// <exception cref="SomeException"/>
        /// <exception cref="SomeException2"/>
        void someCommand(SomeId id, int amount);

        /// <exception cref="SomeException"/>
        SomeClass someQuery(SomeQueryInput query);

        Optional<SomeClass> optMethod(Optional<SomeId> optId);
    }

    public interface SomeInterface2
    {
        OtherClass referenceOtherClass(OtherClass other);

        LegacyType referenceLegacyType(LegacyType legacyType);
    }

    public interface SomeInterface3
    {
        SomeEmptyInterface referenceInterface(SomeEmptyInterface empty);

        OtherInterface referenceOtherInterface(OtherInterface other);
    }
}
