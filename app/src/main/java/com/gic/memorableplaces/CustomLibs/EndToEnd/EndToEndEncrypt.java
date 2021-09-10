package com.gic.memorableplaces.CustomLibs.EndToEnd;


import static com.virgilsecurity.sdk.utils.ConvertionUtils.toBase64String;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.gic.memorableplaces.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.virgilsecurity.android.common.exception.EThreeException;
import com.virgilsecurity.android.common.storage.local.LocalKeyStorage;
import com.virgilsecurity.common.callback.OnCompleteListener;
import com.virgilsecurity.common.exception.NullArgumentException;
import com.virgilsecurity.common.model.Completable;
import com.virgilsecurity.common.model.Data;
import com.virgilsecurity.crypto.foundation.AlgId;
import com.virgilsecurity.crypto.foundation.Base64;
import com.virgilsecurity.crypto.foundation.CtrDrbg;
import com.virgilsecurity.crypto.foundation.KeyAlg;
import com.virgilsecurity.crypto.foundation.KeyAlgFactory;
import com.virgilsecurity.crypto.foundation.KeyAsn1Deserializer;
import com.virgilsecurity.crypto.foundation.KeyAsn1Serializer;
import com.virgilsecurity.crypto.foundation.KeyProvider;
import com.virgilsecurity.crypto.foundation.PrivateKey;
import com.virgilsecurity.crypto.foundation.PublicKey;
import com.virgilsecurity.crypto.foundation.Random;
import com.virgilsecurity.crypto.foundation.RawPublicKey;
import com.virgilsecurity.crypto.foundation.Sha224;
import com.virgilsecurity.crypto.foundation.Sha256;
import com.virgilsecurity.crypto.foundation.Sha384;
import com.virgilsecurity.crypto.foundation.Sha512;
import com.virgilsecurity.sdk.crypto.HashAlgorithm;
import com.virgilsecurity.sdk.crypto.KeyPairType;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.crypto.VirgilKeyPair;
import com.virgilsecurity.sdk.crypto.VirgilPrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilPublicKey;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.crypto.exceptions.DecryptionException;
import com.virgilsecurity.sdk.crypto.exceptions.EncryptionException;
import com.virgilsecurity.sdk.crypto.exceptions.SigningException;
import com.virgilsecurity.sdk.crypto.exceptions.VerificationException;
import com.virgilsecurity.sdk.storage.DefaultKeyStorage;
import com.virgilsecurity.sdk.storage.JsonKeyEntry;
import com.virgilsecurity.sdk.storage.KeyEntry;
import com.virgilsecurity.sdk.storage.KeyStorage;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import kotlinx.coroutines.CoroutineScope;

public class EndToEndEncrypt {
    private static final String TAG = "E2E";
    private Context mContext;
    private boolean useSHA256Fingerprints = false, isDone = false;
    private String KEYSTORE_NAME = "virgil.keystore", identity, publicKey;
    //private final List<VirgilPublicKey> lPublicKeys = new ArrayList<>();

    private KeyStorage keyStorage;
    private LocalKeyStorage localKeyStorage;
    private KeyProvider keyProvider;
    private KeyPairType keyPairType = KeyPairType.ED25519;
    private Random rng;
    private VirgilCrypto crypto;
    private VirgilPublicKey VirgilPublicKey;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    /**
     * Constructor intializing class with basic facilities
     *
     * @param identity identity or userUID of the user
     * @param context  context to call strings etc.
     */
    public EndToEndEncrypt(String identity, String publicKey, Context context) {
        mContext = context;
        this.identity = identity;
        CtrDrbg rng = new CtrDrbg();
        rng.setupDefaults();
        this.rng = rng;
        this.publicKey = publicKey;
        keyStorage = new DefaultKeyStorage(mContext.getFilesDir().getAbsolutePath(), KEYSTORE_NAME);
        crypto = new VirgilCrypto();
        localKeyStorage = new LocalKeyStorage(identity, keyStorage, crypto);
        keyProvider = new KeyProvider();

        this.VirgilPublicKey = InitPublicKey();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

    }

    public EndToEndEncrypt(String identity, Context context) {
        mContext = context;
        this.identity = identity;
        CtrDrbg rng = new CtrDrbg();
        rng.setupDefaults();
        this.rng = rng;
        keyStorage = new DefaultKeyStorage(mContext.getFilesDir().getAbsolutePath(), KEYSTORE_NAME);
        crypto = new VirgilCrypto();
        localKeyStorage = new LocalKeyStorage(identity, keyStorage, crypto);
        keyProvider = new KeyProvider();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

    }

    private VirgilPublicKey InitPublicKey() {
        VirgilPublicKey virgilPublicKey = null;
        try {
            virgilPublicKey = importPublicKey(base64ToBytes(publicKey));
        } catch (CryptoException e) {
            e.printStackTrace();
        }
        return virgilPublicKey;
    }
//
//    private void initPublicKeyList(String publicKey) {
//
//        keyProvider.setRandom(rng);
//        keyProvider.setupDefaults();
//        try {
//            lPublicKeys.add(importPublicKey(GetByteArrayFromData(GetDataFromText(publicKey))));
//        } catch (CryptoException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Imports the Public key from material representation.
     *
     * @param data the public key material representation bytes
     * @return an imported public key
     * @throws CryptoException if key couldn't be imported
     */
    public VirgilPublicKey importPublicKey(byte[] data) throws CryptoException {
        if (data == null) {
            throw new NullArgumentException("data");
        }

        try (KeyProvider keyProvider = new KeyProvider();
             KeyAsn1Deserializer deserializer = new KeyAsn1Deserializer()) {

            deserializer.setupDefaults();
            RawPublicKey rawKey = deserializer.deserializePublicKey(data);
            if (rawKey.cCtx == 0 || rawKey.algId() == AlgId.NONE) {
                throw new CryptoException("Wrong public key format");
            }

            keyProvider.setRandom(rng);
            keyProvider.setupDefaults();

            PublicKey publicKey = keyProvider.importPublicKey(data);
            KeyPairType keyPairType = KeyPairType.fromKey(publicKey);

            byte[] keyId = computePublicKeyIdentifier(publicKey);

            //  Log.d(TAG, "importPublicKey: KeyID: " + Arrays.toString(keyId));
            return new VirgilPublicKey(keyId, publicKey, keyPairType);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
/*
---------------------------------------------------METHODS TO GENERATE AND SAVE USER'S KEYS ON SIGN UP-------------------------------------
 */

    /**
     * Using Virgil Crypto Class to generate a compound KeyPair
     * Getting Private Key from Compound KeyPair, then saving it locally
     * Then, registering the public key from the compound KeyPair into firebase users node
     *
     * @return
     * @throws EThreeException
     */
    public synchronized Completable RegisterAndSaveKeys() {

        return new Completable() {
            @Override
            public void execute() {
                //  Log.d(TAG, "execute: Register User");
                VirgilKeyPair virgilKeyPair;

                try {
                    virgilKeyPair = crypto.generateKeyPair(keyPairType);
                } catch (CryptoException e) {
                    throw new EThreeException(EThreeException.Description.USER_IS_NOT_REGISTERED);
                }
                Data privateKeyData;
                try {
                   // Log.d(TAG, "execute: backuprestore 1 b export private key bytes: " + Arrays.toString(crypto.exportPrivateKey(virgilKeyPair.getPrivateKey())));
                    privateKeyData = GetDataFromByteArray(crypto.exportPrivateKey(virgilKeyPair.getPrivateKey()));
                } catch (CryptoException e) {
                    throw new EThreeException(EThreeException.Description.MISSING_PRIVATE_KEY);
                }
                store(privateKeyData);


                try {
                    myRef.child(mContext.getString(R.string.dbname_users))
                            .child(identity)
                            .child("public_key")
                            .setValue(toBase64String(crypto.exportPublicKey(virgilKeyPair.getPublicKey())));
                } catch (CryptoException e) {
                    throw new EThreeException(EThreeException.Description.VERIFICATION_FAILED);
                }

            }

            @Override
            public void addCallback(@NonNull OnCompleteListener onCompleteListener, CoroutineScope coroutineScope) {
                //  Log.d(TAG, "addCallback: callback 1: ");
                try {
                    execute();
                    onCompleteListener.onSuccess();
                } catch (Throwable throwable) {
                    onCompleteListener.onError(throwable);
                }

            }

            @Override
            public void addCallback(@NonNull OnCompleteListener onCompleteListener) {
                addCallback(onCompleteListener, null);
            }
        };
    }

    public boolean hasLocalPrivateKey() {
        return keyStorage.exists(identity);
    }

    /**
     * Store the private key locally by first checking if key is already stored
     *
     * @param privateKeyData Data of the private key from generated KeyPair
     * @throws EThreeException if key is already stored
     */
    private void store(Data privateKeyData) throws EThreeException {
        // Log.d(TAG, "store: private key data: " + privateKeyData);
        if (keyStorage.exists(identity))
            throw new EThreeException(EThreeException.Description.PRIVATE_KEY_EXISTS);
        keyStorage.store(new JsonKeyEntry(identity, privateKeyData.getValue()));
        //  Log.d(TAG, "store: exists: " + keyStorage.exists(identity));
    }

    public synchronized Completable backupPrivateKey(String password) {

        return new Completable() {
            @Override
            public void execute() {
                if (TextUtils.isEmpty(password)) {
                    throw new EThreeException(EThreeException.Description.valueOf("'password' should not be empty"));
                }
                VirgilKeyPair identityKeyPair = retrieveKeyPair(identity);

                try {
                   // Log.d(TAG, "execute: backuprestore 2 b export private key bytes: " + Arrays.toString(crypto.exportPrivateKey(identityKeyPair.getPrivateKey())));

                    myRef.child(mContext.getString(R.string.dbname_users))
                            .child(identity)
                            .child(mContext.getString(R.string.field_private_key_backup))
                            .setValue(LocalEncrypt(password, crypto.exportPrivateKey(identityKeyPair.getPrivateKey())));
                } catch (CryptoException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void addCallback(@NonNull OnCompleteListener onCompleteListener, CoroutineScope coroutineScope) {
                try {
                    execute();
                    onCompleteListener.onSuccess();
                } catch (Throwable throwable) {
                    onCompleteListener.onError(throwable);
                }
            }

            @Override
            public void addCallback(@NonNull OnCompleteListener onCompleteListener) {
                addCallback(onCompleteListener, null);
            }
        };
    }


    private String LocalEncrypt(String key, byte[] text) {

        byte[] encodeKey = key.getBytes();
        encodeKey = Arrays.copyOf(encodeKey, 16);
       // Log.d(TAG, "LocalEncrypt: backuprestore 3 b enocde key bytes: "+ Arrays.toString(encodeKey));
        Key aesKey = new SecretKeySpec(encodeKey, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(aesKey.getAlgorithm());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new EThreeException(EThreeException.Description.valueOf("Error in cipher"));
        }
        // encrypt the text
        try {
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] encrypted = null;
        try {
            encrypted = cipher.doFinal(text);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
      //  Log.d(TAG, "LocalEncrypt: backuprestore 4 b encrypted bytes: "+ Arrays.toString(encrypted));
      //  Log.d(TAG, "LocalEncrypt: backuprestore 5 b encrypted base 64 string: "+ toBase64String(encrypted));

        return toBase64String(encrypted);
    }

    public synchronized Completable restorePrivateKey(String password) {

        return new Completable() {
            @Override
            public void execute() {
                if (TextUtils.isEmpty(password)) {
                    throw new EThreeException(EThreeException.Description.valueOf("'password' should not be empty"));
                }

                Query query = myRef.child(mContext.getString(R.string.dbname_users))
                        .child(identity)
                        .child(mContext.getString(R.string.field_private_key_backup));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                           // Log.d(TAG, "restorePrivateKey: backuprestore 5 b from firebase base 64 string: "+ snapshot.getValue().toString());

                            byte[] privateKey = LocalDecrypt(password, snapshot.getValue().toString());

//                            Log.d(TAG, "restorePrivateKey: backuprestore 10 privateKey bytes: "+ Arrays.toString(privateKey));

                            Data privateKeyData;
                            try {
                              //  Log.d(TAG, "restorePrivateKey: backuprestore 1 r privateKey final bytes: "+ Arrays.toString(crypto.exportPrivateKey(importPrivateKey(privateKey).getPrivateKey())));

                                privateKeyData = GetDataFromByteArray(crypto.exportPrivateKey(importPrivateKey(privateKey).getPrivateKey()));
                            } catch (CryptoException e) {
                                throw new EThreeException(EThreeException.Description.MISSING_PRIVATE_KEY);
                            }
                            store(privateKeyData);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void addCallback(@NonNull OnCompleteListener onCompleteListener, CoroutineScope coroutineScope) {
                try {
                    execute();
                    onCompleteListener.onSuccess();
                } catch (Throwable throwable) {
                    onCompleteListener.onError(throwable);
                }
            }

            @Override
            public void addCallback(@NonNull OnCompleteListener onCompleteListener) {
                addCallback(onCompleteListener, null);
            }
        };
    }

    private byte[] LocalDecrypt(String key, String password) {
        byte[] encodeKey = key.getBytes();
        encodeKey = Arrays.copyOf(encodeKey, 16);
       // Log.d(TAG, "LocalDecrypt: backuprestore 3 r encode key bytes: "+ Arrays.toString(encodeKey));

        Key aesKey = new SecretKeySpec(encodeKey, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(aesKey.getAlgorithm());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new EThreeException(EThreeException.Description.valueOf("Error in cipher"));
        }
        // decrypt the text
        try {
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] encrypted = null;
        try {
           // Log.d(TAG, "LocalDecrypt: backuprestore 2 r password bytes: "+ Arrays.toString(base64ToBytes(password)));

            encrypted = cipher.doFinal(base64ToBytes(password));
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
       // Log.d(TAG, "LocalDecrypt: backuprestore 4 r decrypted bytes: "+ Arrays.toString(encrypted));
        return encrypted;
    }

//    private void StorePrivateKey(VirgilPrivateKey key, String keyName, String password) {
//        byte[] exportedIdentityKey = new byte[0];
//
//        try {
//            exportedIdentityKey = crypto.exportPrivateKey(key);
//        } catch (CryptoException e) {
//            e.printStackTrace();
//        }
//
//        if (keyName == null) {
//            // Store key in keyknox v1
//            setupCloudKeyStorage(password).store(this.identity, exportedIdentityKey);
//        }
//        } else {
//            // Store key in keyknox v2
//            val brainKeyPair = this.brainKey.generateKeyPair(password)
//            val pullParams = KeyknoxPullParams(this.identity, "e3kit", "backup", keyName)
//            val keyknoxValue = this.keyknoxManager.pullValue(pullParams, listOf(brainKeyPair.publicKey), brainKeyPair.privateKey)
//
//            if (keyknoxValue.value.isNotEmpty() || keyknoxValue.meta.isNotEmpty()) {
//                throw EntryAlreadyExistsException()
//            }
//
//            val params = KeyknoxPushParams(listOf(this.identity), "e3kit", "backup", keyName)
//            val now = Date()
//            val entry = CloudEntry(this.identity, exportedIdentityKey, now, now, mapOf())
//            this.keyknoxManager.pushValue(params, ConvertionUtils.toBytes(Serializer.gson.toJson(entry)), keyknoxValue.keyknoxHash, listOf(brainKeyPair.publicKey), brainKeyPair.privateKey)
//        }
    // }

//    public synchronized Completable restorePrivateKey(keyName: String?, password: String): Completable = object : Completable {
//        override fun execute() {
//            logger.fine("Restore private key $keyName")
//            try {
//                require(password.isNotEmpty()) { "\'password\' should not be empty" }
//
//                val entry = try {
//                    keyManagerCloud.retrieve(keyName, password)
//                } catch (exception: EntryNotFoundException) {
//                    throw EThreeException(EThreeException.Description.NO_PRIVATE_KEY_BACKUP,
//                            exception)
//                }
//
//                val card = lookupManager.lookupCard(this@BackupWorker.identity)
//
//                localKeyStorage.store(entry.data.toData())
//
//                val params = EThreeCore.PrivateKeyChangedParams(card, isNew = false)
//
//                privateKeyChanged(params)
//            } catch (exception: KeyEntryAlreadyExistsException) {
//                throw EThreeException(EThreeException.Description.PRIVATE_KEY_EXISTS,
//                        exception) // FIXME add in swift or remove here
//            }
//        }
//    }
    /*
    --------------------------------------------METHODS TO ENCRYPT GIVEN TEXT------------------------------------------------------
     */

    /**
     * Method to Generate Data object from text and returning encrypted String by using {@link #encryptInternal(Data) method}
     *
     * @param Text Message to be encrypted
     * @return encrypted Base64 String message
     */
    public String authEncrypt(String Text) {


        if (VirgilPublicKey == null) {
            throw new EThreeException(EThreeException.Description.MISSING_PUBLIC_KEY);
        }

        Data data = null;
        try {
            data = GetDataFromText(Text);
        } catch (IllegalArgumentException e) {
            throw new EThreeException(EThreeException.Description.STR_TO_DATA_FAILED, e);
        }


        return (encryptInternal(data)).toBase64String();
    }

    /**
     * Method to call Virgil Crypto Encryption Methods and retrieve Key Pair containing Public and Private keys using
     * {@link #retrieveKeyPair(String) method}
     * This method is called by {@link #authEncrypt(String)}
     *
     * @param data
     * @return Data object of encrypted String
     */
    private Data encryptInternal(Data data) {
        VirgilKeyPair selfKeyPair = retrieveKeyPair(identity);
        List<VirgilPublicKey> lpk = new ArrayList<>();
        lpk.add(selfKeyPair.getPublicKey());

        lpk.add(VirgilPublicKey);
        // Log.d(TAG, "encryptInternal: selfKeyPair: " + selfKeyPair);

        //  Log.d(TAG, "encryptInternal: array: " + lpk);
        byte[] array = null;
        //initPublicKeyList(OtherPublicKey);
        // Log.d(TAG, "encryptInternal: lpk: " + lpk);
        try {
            array = crypto.authEncrypt(data.getValue(), selfKeyPair.getPrivateKey(), lpk);
        } catch (EncryptionException | SigningException e) {
            e.printStackTrace();
        }
        return GetDataFromByteArray(array);
    }

    public static byte[] base64ToBytes(String value) {
        return Base64.decode(value.getBytes());
    }

    public String authDecrypt(String text) throws DecryptionException {
        Data data;
        try {
            data = Data.fromBase64String(text);
        } catch (IllegalArgumentException exception) {
            throw new EThreeException(EThreeException.Description.STR_TO_DATA_FAILED, exception);
        }


        Data decryptedData = null;
        try {
            decryptedData = decryptInternal(data, VirgilPublicKey);
        } catch (CryptoException e) {
            e.printStackTrace();
        }
        if (decryptedData == null) {
            throw new DecryptionException();
        }

        return new String(decryptedData.getValue(), StandardCharsets.UTF_8);
    }

    public String authDecrypt(String text, String OtherPublicKey) throws DecryptionException {
        Data data;
        try {
            data = Data.fromBase64String(text);
        } catch (IllegalArgumentException exception) {
            throw new EThreeException(EThreeException.Description.STR_TO_DATA_FAILED, exception);
        }

        if (OtherPublicKey != null) {
            if (TextUtils.isEmpty(OtherPublicKey)) {
                throw new EThreeException(EThreeException.Description.MISSING_PRIVATE_KEY);
            }
        } else {
            throw new EThreeException(EThreeException.Description.MISSING_PRIVATE_KEY);
        }

        Data decryptedData = null;
        try {
            decryptedData = decryptInternal(data, importPublicKey(base64ToBytes(OtherPublicKey)));
        } catch (CryptoException e) {
            e.printStackTrace();
        }
        if (decryptedData == null) {
            throw new DecryptionException();
        }

        return new String(decryptedData.getValue(), StandardCharsets.UTF_8);
    }

    private Data decryptInternal(Data data, VirgilPublicKey publicKey) throws VerificationException, DecryptionException {
        VirgilKeyPair selfKeyPair = retrieveKeyPair(identity);
        VirgilPublicKey pubKey = publicKey;

        Data encryptedData = null;
        try {
            encryptedData = GetDataFromByteArray(crypto.authDecrypt(data.getValue(), selfKeyPair.getPrivateKey(), pubKey, true));
        } catch (Throwable exception) {
            exception.printStackTrace();
        }
        return encryptedData;
    }



    /*
    -------------------------------------------------Internal private methods by order of interactivity-------------------------------------------------
     */

    /**
     * Method to retreive keyPair from local storage to extract private Key
     *
     * @param identity unique UID to get private key
     * @return Virgil Key Pair
     */
    private VirgilKeyPair retrieveKeyPair(String identity) {

        KeyEntry privateKeyData = keyStorage.load(identity);

        //Log.d(TAG, "retrieveKeyPair: privateKeyData: " + Arrays.toString(privateKeyData.getValue()));
        VirgilKeyPair virgilKeyPair = null;
       // Log.d(TAG, "retrieveKeyPair: private key: " + Arrays.toString(privateKeyData.getValue()));
        virgilKeyPair = importPrivateKey(privateKeyData.getValue());
        //Log.d(TAG, "retrieveKeyPair: virgilKeyPair: " + virgilKeyPair);

        if (virgilKeyPair == null) {
            throw new EThreeException(EThreeException.Description.MISSING_PRIVATE_KEY);
        }

        return virgilKeyPair;

    }

    /**
     * Imports the Private key from material representation.
     *
     * @param data the private key material representation bytes
     * @return imported private key
     * @throws CryptoException if key couldn't be imported
     */
    public VirgilKeyPair importPrivateKey(byte[] data) {
        if (data == null) {
            //  Log.d(TAG, "importPrivateKey: data is null");
            throw new NullArgumentException("data");
        }
        VirgilKeyPair virgilKeyPair = null;
        try (KeyProvider keyProvider = new KeyProvider()) {
            keyProvider.setRandom(rng);
            keyProvider.setupDefaults();
            PrivateKey privateKey = null;
            privateKey = keyProvider.importPrivateKey(data);
            // Log.d(TAG, "importPrivateKey:private key: " + privateKey);
            KeyPairType keyPairType = KeyPairType.fromKey(privateKey);

            PublicKey publicKey = privateKey.extractPublicKey();

            byte[] keyId = computePublicKeyIdentifier(publicKey);


            VirgilPublicKey virgilPublicKey = new VirgilPublicKey(keyId, publicKey, keyPairType);
            VirgilPrivateKey virgilPrivateKey = new VirgilPrivateKey(keyId, privateKey, keyPairType);
            virgilKeyPair = new VirgilKeyPair(virgilPublicKey, virgilPrivateKey);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return virgilKeyPair;
    }

    /**
     * Method to get byte[] from Public Key object
     *
     * @param publicKey public key object
     * @return byte[]
     * @throws CryptoException
     */
    private byte[] computePublicKeyIdentifier(PublicKey publicKey) throws CryptoException {
        try (KeyAsn1Serializer serializer = new KeyAsn1Serializer()) {
            serializer.setupDefaults();

            KeyAlg keyAlg = KeyAlgFactory.createFromKey(publicKey, this.rng);
            try (RawPublicKey rawPublicKey = keyAlg.exportPublicKey(publicKey)) {
                byte[] publicKeyDer = serializer.serializePublicKey(rawPublicKey);
                byte[] hash;
                if (useSHA256Fingerprints) {
                    hash = computeHash(publicKeyDer, HashAlgorithm.SHA256);
                } else {
                    hash = computeHash(publicKeyDer, HashAlgorithm.SHA512);
                    hash = Arrays.copyOfRange(hash, 0, 8);
                }
                return hash;
            } finally {
                if (keyAlg instanceof AutoCloseable) {
                    ((AutoCloseable) keyAlg).close();
                }
            }
        } catch (Exception e) {
            // This should never happen
            throw new CryptoException(e);
        }
    }

    /**
     * Computes hash of given {@code data} according to {@code algorithm}.
     *
     * @param data      data to be hashed.
     * @param algorithm hash {@link HashAlgorithm} to use.
     * @return hash value.
     */
    public byte[] computeHash(byte[] data, HashAlgorithm algorithm) {
        if (data == null) {
            throw new NullArgumentException("data");
        }

        byte[] hashData;
        switch (algorithm) {
            case SHA224:
                try (Sha224 hash = new Sha224()) {
                    hashData = hash.hash(data);
                }
                break;
            case SHA256:
                try (Sha256 hash = new Sha256()) {
                    hashData = hash.hash(data);
                }
                break;
            case SHA384:
                try (Sha384 hash = new Sha384()) {
                    hashData = hash.hash(data);
                }
                break;
            case SHA512:
                try (Sha512 hash = new Sha512()) {
                    hashData = hash.hash(data);
                }
                break;
            default:
                throw new IllegalArgumentException("Please, choose one of: SHA224, SHA256, SHA384, SHA512");
        }
        return hashData;
    }
    // --------------------------------------------------------DATA CONVERTER METHODS-------------------------------------------------------

    /**
     * Get Data from string text
     *
     * @param text String text
     * @return Data object
     */
    private Data GetDataFromText(String text) {
        return new Data(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Get Data object form byte[] array
     *
     * @param array byte[] array
     * @return Data object
     */
    private Data GetDataFromByteArray(byte[] array) {
        return new Data(array);
    }

    /**
     * Get byte[] from Data object
     *
     * @param data Data object
     * @return byte[]
     */
    private byte[] GetByteArrayFromData(Data data) {
        return data.getValue();
    }
}
