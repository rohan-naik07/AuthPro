package com.example.authenticationservice.services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.authenticationservice.entity.Mapping;
import com.example.authenticationservice.entity.Realm;
import com.example.authenticationservice.entity.UserGroup;
import com.example.authenticationservice.error.AuthException;
import com.example.authenticationservice.repositories.MappingRepository;
import com.example.authenticationservice.repositories.RealmRepository;
import com.example.authenticationservice.repositories.UserGroupRepository;


@Service
public class RealmServiceImpl {

    private final RealmRepository realmRepository;
    private final MappingRepository mappingRepository;
    private final UserGroupRepository userGroupRepository;

    @Autowired
    public RealmServiceImpl(
        RealmRepository realmRepository, 
        MappingRepository mappingRepository, 
        UserGroupRepository userGroupRepository
    ) {
        this.realmRepository = realmRepository;
        this.mappingRepository = mappingRepository;
        this.userGroupRepository = userGroupRepository;
    }

    public Realm saveRealm(Realm realm) {
        return realmRepository.save(realm);
    }

    public List<Realm> getAllRealms() {
        return realmRepository.findAll();
    }

    public List<Mapping> getMappingHierarchy(Mapping parent) {
        List<Mapping> mappingListResult = new ArrayList<>();
        List<Mapping> mappingList = mappingRepository.findByParent(parent);
        for (Mapping mapping : mappingList) {
            mappingListResult.addAll(getMappingHierarchy(mapping));
        }
        return mappingListResult;
    }

    public Realm addRealmMapping(Long realmId, String mappingLocation, Long parentMappingId) throws AuthException {
        Realm realm = realmRepository.findById(realmId).orElseThrow(() -> {
            try {
                return new AuthException(new Exception("Could not find realm"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        });
        Mapping parentMapping = mappingRepository.findById(parentMappingId).orElseThrow(() -> {
            try {
                return new AuthException(new Exception("Could not find parent mapping"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        });

        Mapping mapping = new Mapping();
        mapping.setLocation(mappingLocation);
        mapping.setParent(parentMapping);
        mapping.setRealm(realm);
        mapping.setCreatedAt(new Date(System.currentTimeMillis()));
        mappingRepository.save(mapping);
        return realm;
    }

    public void removeRealmMapping(String mappingLocation, Long parentMappingId) throws AuthException {
        Mapping parentMapping = mappingRepository.findById(parentMappingId).orElseThrow(() -> {
            try {
                return new AuthException(new Exception("Could not find parent mapping"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        });
        List<Mapping> mappingList = getMappingHierarchy(parentMapping);
        mappingRepository.deleteAll(mappingList);
    }

    public List<Mapping> getRealmMappings(Long realmId) throws AuthException {
        Realm realm = realmRepository.findById(realmId).orElseThrow(() -> {
            try {
                return new AuthException(new Exception("Could not find realm"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        });
        List<Mapping> mappingList = mappingRepository.findByRealm(realm);
        Mapping rootMapping = mappingList.stream().filter(mapping -> mapping.getParent() == null).findFirst().orElse(null);
        if (rootMapping == null) {
            return new ArrayList<>();
        }
        return getMappingHierarchy(rootMapping);
    }

    public List<Mapping> getMappingsbyParent(Long parentId) throws AuthException {
        Mapping parentMapping = mappingRepository.findById(parentId).orElseThrow(() -> {
            try {
                return new AuthException(new Exception("Could not find parent mapping"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        });
        return getMappingHierarchy(parentMapping);
    }

    public List<Mapping> getMappingsbyLocation(String location) throws AuthException {
        Mapping parentMapping = mappingRepository.findByLocation(location).orElseThrow(() -> {
            try {
                return new AuthException(new Exception("Could not find parent mapping"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        });
        return getMappingHierarchy(parentMapping);
    }

    public void updateMappingsbyUserGroup(Long realmId, Long userGroupId) throws AuthException {
        Realm realm = realmRepository.findById(realmId).orElseThrow(() -> {
            try {
                return new AuthException(new Exception("Could not find realm"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        });
        List<Mapping> mappingList = mappingRepository.findByRealm(realm);
        UserGroup userGroup = userGroupRepository.findById(userGroupId).orElseThrow(() -> {
            try {
                return new AuthException(new Exception("Could not find user group"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        });
        mappingList.forEach(mapping -> mapping.setUserGroup(userGroup));
        mappingRepository.saveAll(mappingList);
    }

    public Realm getRealmById(Long id) {
        return realmRepository.findById(id).orElse(null);
    }

    public void deleteRealmById(Long id) {
        realmRepository.deleteById(id);
    }
}

